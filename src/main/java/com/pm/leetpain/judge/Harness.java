package com.pm.leetpain.judge;

import com.pm.leetpain.Domain.Problem;

import java.io.IOException;
import java.nio.file.Path;

public interface Harness {
    // Prepare files in the given temporary directory (host path) so they are available in /app inside the container
    void prepare(Path tempDir, String userCode, Problem problem) throws IOException;

    // Docker image to use for this language
    String getImage();

    // Command to execute inside the container (should operate on files under /app and /work)
    String getRunCommand();
}
