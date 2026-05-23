package com.example.backend.exception;

/**
 * Exception thrown when user credentials are invalid
 * Specifically for authentication failures
 */
public class InvalidCredentialsException extends RuntimeException {
    private final String errorCode;

    public InvalidCredentialsException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
