package com.example.backend.dto.response;

import lombok.Data;

@Data
public class ThesisApplicationResponseDTO {
    private Long id;
    private String topic;
    private String purpose;
    private String tasks;
    private String techStack;
    private boolean isApproved;
    private String studentId;
    private Long supervisorId;
    private String supervisorName;
    private String departmentName;
    private String approvalStatus;
}
