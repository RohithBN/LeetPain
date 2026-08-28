package com.pm.leetpain.Domain;

public class ExecutionResult {
    private boolean timedOut;
    private int exitCode;
    private String stdout;
    private String stderr;
    private boolean compileError;

    public ExecutionResult(String stdout, String stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public ExecutionResult() {

    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public boolean isCompileError() {
        return compileError;
    }

    public void setCompileError(boolean compileError) {
        this.compileError = compileError;
    }
}
