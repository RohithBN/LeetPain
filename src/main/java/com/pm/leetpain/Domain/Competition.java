package com.pm.leetpain.Domain;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Competition {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Status status;

    private LocalDateTime createdAt;

    public enum Status {
        UPCOMING,
        LIVE,
        COMPLETED
    }
}
