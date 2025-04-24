package com.example.backend.service;

import com.example.backend.data.entity.Teacher;
import com.example.backend.data.entity.ThesisReview;
import com.example.backend.data.entity.ThesisStatement;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.data.repository.ThesisReviewRepository;
import com.example.backend.data.repository.ThesisStatementRepository;
import com.example.backend.dto.request.ThesisReviewRequestDTO;
import com.example.backend.dto.response.ThesisReviewResponseDTO;
import com.example.backend.enums.ApprovalStatus;
import com.example.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ThesisReviewService {

    private final ThesisReviewRepository thesisReviewRepository;
    private final ThesisStatementRepository thesisStatementRepository;
    private final TeacherRepository teacherRepository;

    public ThesisReviewResponseDTO create(ThesisReviewRequestDTO dto) {
        // Check if the thesis statement exists and is owned by the current user
        ThesisStatement thesisStatement = thesisStatementRepository.findById(dto.getThesisStatementId())
                .orElseThrow(() -> new ResourceNotFoundException("Thesis statement not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Teacher teacher = teacherRepository
                .findByUserInfo_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found or not owned by current user"));


        // Create a new review entity
        ThesisReview review = new ThesisReview();
        review.setBody(dto.getTitle());
        review.setBody(dto.getBody());
        review.setThesisStatement(thesisStatement);
        review.setReviewer(teacher);
        review.setApprovalDecision(dto.isApprovalDecision() ? ApprovalStatus.APPROVED.name() : ApprovalStatus.REJECTED.name());
        review.setDateOfUpload(LocalDateTime.now());

        // Save the review
        var savedReview = thesisReviewRepository.save(review);

        return toDto(savedReview);
    }

    private ThesisReviewResponseDTO toDto(ThesisReview savedReview) {
        ThesisReviewResponseDTO dto = new ThesisReviewResponseDTO();
        dto.setBody(savedReview.getBody());
        dto.setDateOfUpload(savedReview.getDateOfUpload());
        dto.setId(savedReview.getId());
        dto.setReviewerName(savedReview.getReviewer().getUserInfo().getFirstName() + " " + savedReview.getReviewer().getUserInfo().getLastName());
        dto.setApprovalDecision(savedReview.getApprovalDecision());
        return dto;
    }
}
