package com.example.backend.controller;

import com.example.backend.dto.UserInfoDTO;
import com.example.backend.dto.response.StudentCountThesisReviewDecisionDTO;
import com.example.backend.dto.response.SupervisorSuccessStatsDTO;
import com.example.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/supervisor/{supervisorId}/successful-defences")
    public ResponseEntity<SupervisorSuccessStatsDTO> getSuccessfulDefencesBySupervisor(@PathVariable Long supervisorId) {
        return ResponseEntity.ok(studentService.countSuccessfulDefendedBySupervisor(supervisorId));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/graduated")
    public ResponseEntity<List<UserInfoDTO>> getGraduatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(studentService.getGraduatedStudentsBetween(start, end));
    }

    /**
     * Get total student count based on user role.
     * Requires authentication. Returns all students if ADMIN, or only supervised students if TEACHER.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalStudentCount() {
        long count = studentService.getTotalStudentCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get student list based on user role.
     * Requires authentication. Returns all students if ADMIN, or only supervised students if TEACHER.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    @GetMapping("/list")
    public ResponseEntity<List<UserInfoDTO>> getStudentList() {
        List<UserInfoDTO> students = studentService.getStudentList();
        return ResponseEntity.ok(students);
    }
}
