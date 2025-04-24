package com.example.backend.controller;

import com.example.backend.dto.request.ThesisReviewRequestDTO;
import com.example.backend.dto.response.ThesisReviewResponseDTO;
import com.example.backend.service.ThesisReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/thesis-reviews")
@RequiredArgsConstructor
public class ThesisReviewController {
    private final ThesisReviewService thesisReviewService;

    @PostMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ThesisReviewResponseDTO> createReview(@RequestBody ThesisReviewRequestDTO dto) {
        ThesisReviewResponseDTO created = thesisReviewService.create(dto);
        return ResponseEntity.status(201).body(created); // simple 201, no Location header
    }
}
