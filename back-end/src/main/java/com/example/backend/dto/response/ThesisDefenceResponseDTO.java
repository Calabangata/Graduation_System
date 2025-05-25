package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesisDefenceResponseDTO {
    private Long id;
    private String scheduledMessage;
    private Map<String, String> students; // facultyNumber -> studentName
    private Map<String, String> teachers; // email -> teacherName
}
