package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateThesisDefenceRequestDTO {
    private LocalDateTime date; // Scheduled defence date and time
    private String location;    // Optional
    private Long departmentId;  // New field for department link
    private List<String> studentIds; //OPTIONAL
    private List<Long> teacherIds; //OPTIONAL
}
