package com.example.backend.dto.response;

import lombok.Data;

@Data
public class ThesisApplicationResponseDTO {
    private String topic;
    private String purpose;
    private String tasks;
    private String techStack;
    private boolean isApproved;
    private Long supervisorId;
    private String supervisorName;
    private String departmentName;
    private String approvalStatus;
}
