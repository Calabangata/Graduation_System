package com.example.backend.dto.request;

import lombok.Data;

@Data

public class VoteOnThesisDTO {
    private Long teacherId;
    private Long thesisApplicationId;
    private boolean approved;
}
