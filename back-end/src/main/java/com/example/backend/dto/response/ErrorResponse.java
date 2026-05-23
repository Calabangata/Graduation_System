package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Error response DTO for authentication errors
 * Sends error code and message to frontend
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
}
