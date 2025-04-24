package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesisReviewRequestDTO {
    private String title;
    private String body;
    private Long thesisStatementId;
    private boolean approvalDecision; // "APPROVED", "REJECTED"
}
