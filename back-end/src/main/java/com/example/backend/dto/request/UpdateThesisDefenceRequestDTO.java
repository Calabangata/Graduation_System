package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateThesisDefenceRequestDTO {
    private LocalDateTime date;          // Optional new date
    private String location;             // Optional new location
}
