package com.pm.leetpain.judge;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.pm.leetpain.Domain.ExecutionResult;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.Domain.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@Component
public class GenericRuntimeExecutor implements RuntimeExecutor {
    private static final Logger log = LoggerFactory.getLogger(GenericRuntimeExecutor.class);
    private final DockerExecutor dockerExecutor;
    private final HarnessFactory harnessFactory;

    public GenericRuntimeExecutor(DockerExecutor dockerExecutor, HarnessFactory harnessFactory) {
        this.dockerExecutor = dockerExecutor;
        this.harnessFactory = harnessFactory;
    }

    @Override
    public ExecutionResult execute(String code, String language, Problem problem) {
        ExecutionResult result = new ExecutionResult();
        Path tempDir = null;
        String containerId = null;
        try {
            tempDir = Files.createTempDirectory("submission-");

            Harness harness = harnessFactory.getHarnessForLanguage(language);
            harness.prepare(tempDir, code, problem);

            String command = harness.getRunCommand();

            CreateContainerResponse container = dockerExecutor.createContainerWithBind(harness.getImage(), command, tempDir.toString(), true);
            containerId = container.getId();

            List<TestCase> testCases = problem.getTestCases();
            for (int i = 0; i < testCases.size(); i++) {
                TestCase tc = testCases.get(i);
                ExecutionResult execResult = dockerExecutor.runContainerWithInput(
                        containerId,
                        tc.getInput(),
                        5L
                );
                if (execResult.isTimedOut()) {
                    result.setCompileError("TIME_LIMIT_EXCEEDED");
                    result.setFailedTestCaseIndex(i);
                    return result;
                }

                if (execResult.getExitCode() != 0) {
                    // If compile error marker was printed, forward as compilation error
                    String stdout = execResult.getStdout();
                    if (stdout != null && stdout.contains("COMPILE_ERROR:")) {
                        result.setCompileError("COMPILATION_ERROR");
                        result.setStderr(execResult.getStderr() != null ? execResult.getStderr() : execResult.getStdout());
                        result.setFailedTestCaseIndex(i);
                        return result;
                    }

                    result.setCompileError("RUNTIME_ERROR");
                    result.setStderr(execResult.getStderr());
                    result.setFailedTestCaseIndex(i);
                    return result;
                }
                String stdout = execResult.getStdout();
                if (stdout != null && stdout.contains("COMPILE_ERROR:")) {
                    result.setCompileError("COMPILATION_ERROR");
                    result.setStderr(execResult.getStderr() != null ? execResult.getStderr() : execResult.getStdout());
                    result.setFailedTestCaseIndex(i);
                    return result;
                }
                String actualOutput = normalize(execResult.getStdout());
                String expectedOutput = normalize(tc.getExpectedOutput());
                if (!actualOutput.equals(expectedOutput)) {
                    result.setStatus("WRONG_ANSWER");
                    result.setExpectedOutput(expectedOutput);
                    result.setActualOutput(actualOutput);
                    result.setFailedTestCaseIndex(i);
                    return result;
                }
            }
            result.setStatus("ACCEPTED");
        } catch (IOException | InterruptedException e) {
            log.error("Error executing code: {}", e.getMessage());
            result.setCompileError("INTERNAL_ERROR");
        } finally {
            if (containerId != null) {
                dockerExecutor.removeContainer(containerId);
            }
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(java.io.File::delete);
                } catch (IOException e) {
                    log.error("Error cleaning up temporary directory: {}", e.getMessage());
                }
            }
        }
        return result;
    }

    private String normalize(String out) {
        if (out == null) return "";
        return out.trim().replaceAll("\r\n", "\n");
    }
}
