package com.example.backend.service;

import com.example.backend.data.repository.StudentRepository;
import com.example.backend.dto.response.StudentCountThesisReviewDecisionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserInfoService userInfoService;
    private final StudentRepository studentRepository;

    public void deactivateStudentAccount(String email) {
        userInfoService.deactivateUser(email);
    }

    public StudentCountThesisReviewDecisionDTO countStudentsByReviewDecision(String status) {
        String decision = status.toUpperCase();
        long count = studentRepository.countStudentsByReviewDecision(decision);
        String msg = String.format("There are %d students with %s thesis review.", count, decision.toLowerCase());

        return new StudentCountThesisReviewDecisionDTO(decision, count, msg);
    }
}
