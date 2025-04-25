package com.example.backend.dto.request;

import lombok.Data;

@Data

public class SubmitThesisApplicationDTO {
    private String topic;
    private String purpose;
    private String tasks;
    private String techStack;
    private String studentId;
}
