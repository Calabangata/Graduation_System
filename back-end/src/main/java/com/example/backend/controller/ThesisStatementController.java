package com.example.backend.controller;

import com.example.backend.dto.ThesisStatementDTO;
import com.example.backend.dto.request.GradeThesisDTO;
import com.example.backend.service.ThesisStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/grade-range")
    public ResponseEntity<List<ThesisStatementDTO>> getStatementsByGradeRange(@RequestParam int minGrade, @RequestParam int maxGrade) {
        List<ThesisStatementDTO> results = thesisStatementService.findByGradeRange(minGrade, maxGrade);
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/grade")
    public ResponseEntity<String> gradeThesis(@RequestBody GradeThesisDTO dto) {
        thesisStatementService.gradeThesis(dto);
        return ResponseEntity.ok("Thesis graded successfully.");
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{statementId}")
    public ResponseEntity<Void> deleteStatement(@PathVariable Long statementId) {
        thesisStatementService.deleteThesisStatement(statementId);
        return ResponseEntity.noContent().build();
    }
}
