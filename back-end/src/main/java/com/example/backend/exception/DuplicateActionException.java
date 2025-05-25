package com.example.backend.exception;

public class DuplicateActionException extends RuntimeException {
    public DuplicateActionException(String message) {
        super(message);
    }
}
