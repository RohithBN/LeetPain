package com.pm.leetpain.judge;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.pm.leetpain.Domain.ExecutionResult;
import com.pm.leetpain.Domain.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Slf4j
@Component
public class JavaRuntimeExecutor implements RuntimeExecutor{
    private final DockerExecutor dockerExecutor;

    public JavaRuntimeExecutor(DockerExecutor dockerExecutor) {
        this.dockerExecutor = dockerExecutor;
    }

    @Override
    public ExecutionResult execute(String code, String language, Problem problem) {
        ExecutionResult result = new ExecutionResult();
        Path tempDir = null;
        String containerId = null;
        try {
            tempDir = Files.createTempDirectory("submission-");
            Path src = tempDir.resolve("Main.java");
            Files.write(src, code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            // Command executed inside container: compile into /work, if compile error print marker then exit; otherwise run Main
            String command = "mkdir -p /work && javac /app/Main.java -d /work 2> /work/compile.err || true && if [ -s /work/compile.err ]; then echo 'COMPILE_ERROR:'; cat /work/compile.err; else java -cp /work Main; fi";

            CreateContainerResponse container = dockerExecutor.createContainerWithBind(DockerExecutor.JAVA_IMAGE, command, tempDir.toString(), true);
            containerId = container.getId();
            dockerExecutor.startContainer(containerId);
            log.debug("Started container {} for execution", containerId);

            Integer exitCode = dockerExecutor.waitForContainer(containerId, 5L);
            boolean timedOut = exitCode == null;
            if (timedOut) {
                try { dockerExecutor.killContainer(containerId); } catch (Exception ignored) {}
                result.setTimedOut(true);
                result.setExitCode(-1);
            } else {
                result.setTimedOut(false);
                result.setExitCode(exitCode);
            }

            log.debug("Getting container logs for {}", containerId);
            result = dockerExecutor.getContainerLogs(containerId);


            String combinedOutput = (result.getStdout() == null ? "" : result.getStdout()) +
                    (result.getStderr() == null ? "" : result.getStderr());
            if (combinedOutput.contains("COMPILE_ERROR:")) {
                result.setCompileError(true);
                // extract after marker
                int idx = combinedOutput.indexOf("COMPILE_ERROR:");
                String after = combinedOutput.substring(idx + "COMPILE_ERROR:".length()).trim();
                result.setStderr(after);
                result.setStdout("");
            } else {
                result.setCompileError(false);
                log.debug("No compile error found.");

            }
            log.debug("Execution result: exitCode={}, timedOut={}, compileError={} , stdout={} , stderr={}", result.getExitCode(), result.isTimedOut(), result.isCompileError(), result.getStdout(), result.getStderr());

        } catch (IOException e) {
            result.setStdout("");
            result.setStderr(e.getMessage());
            result.setExitCode(-1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (containerId != null) {
                try { dockerExecutor.removeContainer(containerId); } catch (Exception ignored) {}
            }
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                            .sorted((a,b)->b.compareTo(a))
                            .forEach(p-> { try { Files.deleteIfExists(p); } catch (IOException ignored) {} });
                } catch (IOException ignored) {}
            }
        }

        return result;
    }
}
