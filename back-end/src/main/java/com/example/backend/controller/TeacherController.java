package com.example.backend.controller;

import com.example.backend.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping("/{email}")
    public ResponseEntity<Void> deactivateTeacherAccount(@PathVariable String email) {
        teacherService.deactivateTeacherAccount(email);
        return ResponseEntity.noContent().build();
    }
}
