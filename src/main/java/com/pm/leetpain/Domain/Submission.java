package com.pm.leetpain.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Submission {

    public Submission(){

    }

    private Long id;

    private Long userId;

    private Long problemId;

    // Nullable for normal practice submissions
    private Long competitionId;

    private String language;

    private String sourceCode;

    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getMemoryUsedKb() {
        return memoryUsedKb;
    }

    public void setMemoryUsedKb(Integer memoryUsedKb) {
        this.memoryUsedKb = memoryUsedKb;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    private Integer executionTimeMs;

    private Integer memoryUsedKb;

    public Submission(Long id , Long problemId, String language, String sourceCode) {
        this.id=id;
        this.problemId = problemId;
        this.language = language;
        this.sourceCode = sourceCode;
    }

    private LocalDateTime submittedAt;

    private LocalDateTime completedAt;

    public Submission(Long problemId, Status status) {
        this.problemId = problemId;
        this.status = status;
    }

    public enum Status {
        QUEUED,
        RUNNING,
        ACCEPTED,
        WRONG_ANSWER,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        RUNTIME_ERROR,
        COMPILATION_ERROR
    }
}