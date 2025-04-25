package com.example.backend.service;

import com.example.backend.data.entity.ThesisApplication;
import com.example.backend.data.entity.ThesisStatement;
import com.example.backend.data.repository.ThesisApplicationRepository;
import com.example.backend.data.repository.ThesisStatementRepository;
import com.example.backend.dto.ThesisStatementDTO;
import com.example.backend.enums.ApprovalStatus;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ForbiddenActionException;
import com.example.backend.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThesisStatementService {

    private final ThesisApplicationRepository thesisApplicationRepository;
    private final ThesisStatementRepository thesisStatementRepository;

    @Transactional
    public ThesisStatementDTO create(ThesisStatementDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        ThesisApplication application = thesisApplicationRepository
                .findByStudent_UserInfo_EmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis application not found or not owned by current user"));

        if (application.getThesisApproval().getStatus() != ApprovalStatus.APPROVED) {
            throw new ForbiddenActionException("Thesis application is not approved");
        }

        if (application.getThesisStatement() != null) {
            throw new ConflictException("Thesis statement already exists for this application");
        }

        ThesisStatement statement = new ThesisStatement();
        statement.setTitle(dto.getTitle());
        statement.setBody(dto.getBody());
        statement.setGrade(null); // explicitly null
        statement.setThesisApplication(application);
        ThesisStatement saved = thesisStatementRepository.save(statement);
        thesisApplicationRepository.save(application);

        return toDto(saved);
    }

    private ThesisStatementDTO toDto(ThesisStatement statement) {
        ThesisStatementDTO dto = new ThesisStatementDTO();
        dto.setTitle(statement.getTitle());
        dto.setBody(statement.getBody());
        return dto;
    }
}
