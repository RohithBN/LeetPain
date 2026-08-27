package com.pm.leetpain.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Submission {

    private Long id;

    private Long userId;

    private Long problemId;

    // Nullable for normal practice submissions
    private Long competitionId;

    private String language;

    private String sourceCode;

    private Status status;

    private Integer executionTimeMs;

    private Integer memoryUsedKb;

    private LocalDateTime submittedAt;

    private LocalDateTime completedAt;

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