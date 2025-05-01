package com.example.backend.controller;

import com.example.backend.dto.response.StudentCountThesisReviewDecisionDTO;
import com.example.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping("/{email}")
    public ResponseEntity<Void> deactivateStudentAccount(@PathVariable String email) {
        studentService.deactivateStudentAccount(email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/review-count")
    public ResponseEntity<StudentCountThesisReviewDecisionDTO> countByReviewStatus(@RequestParam String status) {
        return ResponseEntity.ok(studentService.countStudentsByReviewDecision(status));
    }
}
