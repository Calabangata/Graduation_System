package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentDTO {
    @NotBlank(message = "Department name is required")
    private String departmentName;
}
