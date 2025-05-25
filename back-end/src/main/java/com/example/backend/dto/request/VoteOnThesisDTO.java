package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteOnThesisDTO {
    private Long thesisApplicationId;
    private boolean approved;
}
