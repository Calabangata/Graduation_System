package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesisReviewResponseDTO {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime dateOfUpload;
    private String reviewerName;
    private String approvalDecision; // e.g. "APPROVED" or "REJECTED"
}
