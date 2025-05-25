package com.example.backend.enums;

import com.example.backend.exception.BadRequestException;

public enum ApprovalStatus {
    PENDING, APPROVED, REJECTED;

    public static ApprovalStatus fromString(String value) {
        if (value == null) {
            throw new BadRequestException("Approval status cannot be null");
        }
        return switch (value.toUpperCase()) {
            case "APPROVED" -> APPROVED;
            case "PENDING" -> PENDING;
            case "REJECTED" -> REJECTED;
            default -> throw new BadRequestException("Invalid approval status: " + value);
        };
    }

}
