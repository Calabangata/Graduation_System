package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentCountThesisReviewDecisionDTO {
    private String decision;
    private long count;
    private String message;
}
