package com.example.backend.controller;

import com.example.backend.dto.request.SubmitThesisApplicationDTO;
import com.example.backend.dto.response.ThesisApplicationResponseDTO;
import com.example.backend.service.ThesisApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/thesis-applications")
@RequiredArgsConstructor
public class ThesisApplicationController {

    private final ThesisApplicationService thesisApplicationService;

    @PreAuthorize("hasAnyRole('TEACHER')")
    @PostMapping("/submit")
    public ResponseEntity<ThesisApplicationResponseDTO> submitApplication(@RequestBody SubmitThesisApplicationDTO dto) {
        return ResponseEntity.ok(thesisApplicationService.submitApplication(dto));
    }
}
