package com.example.backend.service;

import com.example.backend.data.entity.Teacher;
import com.example.backend.data.entity.ThesisReview;
import com.example.backend.data.entity.ThesisStatement;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.data.repository.ThesisReviewRepository;
import com.example.backend.data.repository.ThesisStatementRepository;
import com.example.backend.dto.request.ThesisReviewRequestDTO;
import com.example.backend.dto.response.ThesisReviewResponseDTO;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThesisReviewServiceTest {

    @Mock
    private ThesisReviewRepository thesisReviewRepository;
    @Mock
    private ThesisStatementRepository thesisStatementRepository;
    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private ThesisReviewService reviewService;


    private void mockSecurity(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of())
        );
    }

    @Test
    void createReview_shouldSucceed_whenValidInput() {
        ThesisReviewRequestDTO dto = new ThesisReviewRequestDTO();
        dto.setTitle("My Title");
        dto.setBody("Review body");
        dto.setThesisStatementId(1L);
        dto.setApprovalDecision(true);
        ThesisStatement statement = new ThesisStatement();
        statement.setId(1L);
        Teacher teacher = new Teacher();
        UserInfo info = new UserInfo();
        info.setFirstName("John");
        info.setLastName("Doe");
        teacher.setUserInfo(info);

        when(thesisStatementRepository.findById(1L)).thenReturn(Optional.of(statement));
        when(teacherRepository.findByUserInfo_Email("teacher@example.com")).thenReturn(Optional.of(teacher));
        when(thesisReviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        mockSecurity("teacher@example.com");

        ThesisReviewResponseDTO result = reviewService.create(dto);
        assertNotNull(result);
        assertEquals("My Title", result.getTitle());
        assertEquals("APPROVED", result.getApprovalDecision());
        assertEquals("John Doe", result.getReviewerName());
    }

    @Test
    void createReview_shouldFail_whenStatementNotFound() {
        when(thesisStatementRepository.findById(1L)).thenReturn(Optional.empty());
        ThesisReviewRequestDTO dto = new ThesisReviewRequestDTO();
        dto.setThesisStatementId(1L);
        assertThrows(ResourceNotFoundException.class, () -> reviewService.create(dto));
    }

    @Test
    void createReview_shouldFail_whenReviewAlreadyExists() {
        ThesisStatement statement = new ThesisStatement();
        statement.setThesisReview(new ThesisReview()); // already has a review
        when(thesisStatementRepository.findById(1L)).thenReturn(Optional.of(statement));
        ThesisReviewRequestDTO dto = new ThesisReviewRequestDTO();
        dto.setThesisStatementId(1L);
        assertThrows(ConflictException.class, () -> reviewService.create(dto));
    }

    @Test
    void createReview_shouldFail_whenReviewerNotFound() {
        ThesisStatement statement = new ThesisStatement();
        statement.setId(1L);
        when(thesisStatementRepository.findById(1L)).thenReturn(Optional.of(statement));
        when(teacherRepository.findByUserInfo_Email("bad@example.com")).thenReturn(Optional.empty());
        ThesisReviewRequestDTO dto = new ThesisReviewRequestDTO();
        dto.setThesisStatementId(1L);
        mockSecurity("bad@example.com");
        assertThrows(ResourceNotFoundException.class, () -> reviewService.create(dto));
    }
}
