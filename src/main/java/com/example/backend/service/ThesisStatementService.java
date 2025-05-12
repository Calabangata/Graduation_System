package com.example.backend.service;

import com.example.backend.data.entity.*;
import com.example.backend.data.repository.*;
import com.example.backend.dto.ThesisStatementDTO;
import com.example.backend.dto.request.GradeThesisDTO;
import com.example.backend.enums.ApprovalStatus;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ForbiddenActionException;
import com.example.backend.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThesisStatementService {

    private final ThesisApplicationRepository thesisApplicationRepository;
    private final ThesisStatementRepository thesisStatementRepository;
    private final ThesisDefenceRepository thesisDefenceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ThesisReviewRepository thesisReviewRepository;
    private static final Logger log = LoggerFactory.getLogger(ThesisStatementService.class);

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
        statement.setGrade(null);
        statement.setThesisApplication(application);
        ThesisStatement saved = thesisStatementRepository.save(statement);
        thesisApplicationRepository.save(application);

        return toDto(saved);
    }

    @Transactional
    public void gradeThesis(GradeThesisDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByUserInfo_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        ThesisDefence defence = thesisDefenceRepository.findByStudents_Id(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No thesis defence found for this student"));
        if (defence.getDate().isAfter(LocalDateTime.now())) {
            throw new ConflictException("Thesis defence has not occurred yet. Grading not allowed before defence.");
        }
        if (defence.getTeachers().stream()
                .noneMatch(t -> t.getId().equals(teacher.getId()))) {
            throw new ConflictException("You are not assigned to this student's defence session");
        }

        ThesisApplication activeApplication = student.getThesisApplications().stream()
                .filter(ThesisApplication::isActive)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Student does not have an active thesis application"));

        ThesisStatement thesisStatement = thesisStatementRepository.findByThesisApplicationId(activeApplication.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No thesis statement found for the student's active application"));

        if (thesisStatement.getGrade() != null) {
            throw new ConflictException("Thesis statement is already graded");
        }

        if (dto.getGrade() < 2 || dto.getGrade() > 6) {
            throw new ConflictException("Grade must be between 2 and 6");
        }

        thesisStatement.setGrade(dto.getGrade());
        thesisStatementRepository.save(thesisStatement);
        if (dto.getGrade() >= 3) {
            student.setGraduated(true);
            studentRepository.save(student);
            log.info("Student [{}] has graduated after successful thesis grading.", student.getId());
            activeApplication.setActive(false);
            thesisApplicationRepository.save(activeApplication);
        }
    }

    private ThesisStatementDTO toDto(ThesisStatement statement) {
        ThesisStatementDTO dto = new ThesisStatementDTO();
        dto.setTitle(statement.getTitle());
        dto.setBody(statement.getBody());
        return dto;
    }

    public List<ThesisStatementDTO> findByGradeRange(int minGrade, int maxGrade) {
        if (minGrade < 2 || maxGrade > 6 || minGrade > maxGrade) {
            throw new BadRequestException("Invalid grade range: min must be >= 2, max must be <= 6, and min must be <= max.");
        }

        List<ThesisStatement> results = thesisStatementRepository.findAllByGradeBetween(minGrade, maxGrade);
        return results.stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteThesisStatement(Long statementId) {
        ThesisStatement statement = thesisStatementRepository.findById(statementId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis statement not found"));

        if (statement.getGrade() != null) {
            throw new ConflictException("Thesis statement cannot be deleted after grading");
        }

        if(statement.getThesisReview() != null) {
            ThesisReview review = statement.getThesisReview();
            thesisReviewRepository.delete(review);
            log.info("Thesis review with ID [{}] deleted successfully.", review.getId());
        }
        thesisStatementRepository.delete(statement);
    }
}
