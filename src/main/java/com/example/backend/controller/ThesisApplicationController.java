package com.example.backend.controller;

import com.example.backend.dto.request.SubmitThesisApplicationDTO;
import com.example.backend.dto.request.VoteOnThesisDTO;
import com.example.backend.dto.response.ThesisApplicationResponseDTO;
import com.example.backend.service.ThesisApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long applicationId) {
        thesisApplicationService.deleteApplication(applicationId);
        //return ResponseEntity.ok("Application deleted successfully.");
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/vote")
    public ResponseEntity<String> voteOnThesis(@RequestBody VoteOnThesisDTO dto) {
        thesisApplicationService.voteOnThesis(dto);
        return ResponseEntity.ok("Vote recorded successfully.");
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/evaluate/{applicationId}")
    public ResponseEntity<String> evaluateVotes(@PathVariable Long applicationId) {
        thesisApplicationService.evaluateVotes(applicationId);
        return ResponseEntity.ok("Evaluation completed.");
    }
}
