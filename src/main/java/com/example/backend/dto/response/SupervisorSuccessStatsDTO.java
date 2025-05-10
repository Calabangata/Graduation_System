package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupervisorSuccessStatsDTO {
    private long successfulDefences;
    private String message;
}
