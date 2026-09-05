package com.pm.leetpain.judge;

import com.pm.leetpain.Domain.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class JavaHarness implements Harness {

    private static final String PLACEHOLDER = "{{USER_CODE}}";

    @Override
    public void prepare(Path tempDir, String userCode, Problem problem) throws IOException {
        // sanitize user code and build the final executable source using problem-specific driver harness
        String executable = buildExecutableCode(userCode, "java", problem);
        Path src = tempDir.resolve("Main.java");
        Files.write(src, executable.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getImage() {
        return DockerExecutor.JAVA_IMAGE;
    }

    @Override
    public String getRunCommand() {
        // compile to /work; if compile errors exist print marker then exit; otherwise run Main
        return "mkdir -p /work && javac /app/Main.java -d /work 2> /work/compile.err || true && if [ -s /work/compile.err ]; then echo 'COMPILE_ERROR:'; cat /work/compile.err; else java -cp /work Main; fi";
    }

    /**
     * Combine a per-problem driver harness with the user's method-only stub.
     */
    public String buildExecutableCode(String userCode, String language, Problem problem) throws IOException {
        String sanitized = sanitizeUserCode(userCode);
        if (problem == null || problem.getDriverHarnesses() == null) {
            throw new IOException("No driver harness map found for this problem.");
        }

        try {
            Problem.Language lang = Problem.Language.valueOf(language.trim().toUpperCase());
            String template = problem.getDriverHarnesses().get(lang);
            if (template == null || template.isBlank()) {
                throw new IOException("Missing driver harness template for language: " + language);
            }
            if (!template.contains(PLACEHOLDER)) {
                throw new IOException("Driver harness template must contain placeholder: " + PLACEHOLDER);
            }
            return template.replace(PLACEHOLDER, sanitized);
        } catch (IllegalArgumentException e) {
            throw new IOException("Unsupported language enum: " + language, e);
        }
    }

    private String sanitizeUserCode(String userCode) throws IOException {
        if (userCode == null) return "";
        // remove package declarations
        String out = userCode.replaceAll("(?m)^\\s*package\\s+[\\w\\.]+;\\s*", "");
        // LeetCode-style contract: user submits method(s), not full classes/main.
        if (out.matches("(?s).*\\b(class|interface|enum|record)\\b.*")) {
            throw new IOException("Submission must contain only method-level code; class/interface/enum declarations are not allowed.");
        }
        if (out.matches("(?s).*\\bSystem\\.exit\\s*\\(.*")) {
            throw new IOException("System.exit is not allowed in submissions.");
        }
        return out;
    }
}
