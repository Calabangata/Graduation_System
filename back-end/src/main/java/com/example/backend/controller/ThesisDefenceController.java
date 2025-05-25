package com.example.backend.controller;

import com.example.backend.dto.request.CreateThesisDefenceRequestDTO;
import com.example.backend.dto.request.UpdateThesisDefenceRequestDTO;
import com.example.backend.dto.response.ThesisDefenceResponseDTO;
import com.example.backend.service.ThesisDefenceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thesis-defences")
@AllArgsConstructor
public class ThesisDefenceController {

    private ThesisDefenceService thesisDefenceService;

    //endpoint for new thesis defence
    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ThesisDefenceResponseDTO> createThesisDefence(@RequestBody CreateThesisDefenceRequestDTO requestDTO) {
        ThesisDefenceResponseDTO response = thesisDefenceService.createDefence(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{defenceId}/assign-students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ThesisDefenceResponseDTO> assignStudentsToDefence(@PathVariable Long defenceId, @RequestBody List<String> facultyNumbers) {
        ThesisDefenceResponseDTO response = thesisDefenceService.assignStudents(defenceId, facultyNumbers);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{defenceId}/assign-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ThesisDefenceResponseDTO> assignTeachersToDefence(@PathVariable Long defenceId, @RequestBody List<String> emails) {
        ThesisDefenceResponseDTO response = thesisDefenceService.assignTeachers(defenceId, emails);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{defenceId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ThesisDefenceResponseDTO> updateDefence(@PathVariable Long defenceId, @RequestBody UpdateThesisDefenceRequestDTO requestDTO) {
        ThesisDefenceResponseDTO response = thesisDefenceService.updateDefence(defenceId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{defenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDefence(@PathVariable Long defenceId) {
        thesisDefenceService.deleteDefence(defenceId);
        return ResponseEntity.noContent().build();
    }

}
