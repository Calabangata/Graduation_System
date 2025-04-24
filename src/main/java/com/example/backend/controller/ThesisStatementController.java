package com.example.backend.controller;

import com.example.backend.dto.ThesisStatementDTO;
import com.example.backend.service.ThesisStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/thesis-statements")
public class ThesisStatementController {

    private final ThesisStatementService thesisStatementService;

    @PostMapping("/new")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ThesisStatementDTO> createStatement(@RequestBody ThesisStatementDTO dto) {
        ThesisStatementDTO created = thesisStatementService.create(dto);
        return ResponseEntity.status(201).body(created); // simple 201, no Location header
    }
}
