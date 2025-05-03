package com.example.backend.service;

import com.example.backend.data.repository.StudentRepository;
import com.example.backend.dto.response.StudentCountThesisReviewDecisionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private UserInfoService userInfoService;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void deactivateStudentAccount_shouldDelegateToUserInfoService() {
        String email = "test@student.com";
        studentService.deactivateStudentAccount(email);
        verify(userInfoService).deactivateUser(email);
    }

    @Test
    void countStudentsByReviewDecision_shouldReturnFormattedDto() {
        String decision = "REJECTED";
        when(studentRepository.countStudentsByReviewDecision(decision)).thenReturn(3L);
        StudentCountThesisReviewDecisionDTO result = studentService.countStudentsByReviewDecision(decision);
        assertEquals("REJECTED", result.getDecision());
        assertEquals(3L, result.getCount());
        assertEquals("There are 3 students with rejected thesis review.", result.getMessage());
    }
}
