package com.pm.leetpain.Domain;

public class ExecutionResult {
    private boolean timedOut;
    private int exitCode;
    private String stdout;
    private String stderr;
    private String compileError;
    private String ExpectedOutput;

    public String getExpectedOutput() {
        return ExpectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        ExpectedOutput = expectedOutput;
    }

    public String getActualOutput() {
        return ActualOutput;
    }

    public void setActualOutput(String actualOutput) {
        ActualOutput = actualOutput;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    private String ActualOutput;
    private String Status;

    public Integer getFailedTestCaseIndex() {
        return failedTestCaseIndex;
    }

    public void setFailedTestCaseIndex(Integer failedTestCaseIndex) {
        this.failedTestCaseIndex = failedTestCaseIndex;
    }

    private Integer failedTestCaseIndex;

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

    public String getCompileError() {
        return compileError;
    }

    public void setCompileError(String compileError) {
        this.compileError = compileError;
    }
}
