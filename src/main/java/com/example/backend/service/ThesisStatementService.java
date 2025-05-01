package com.example.backend.service;

import com.example.backend.data.entity.Student;
import com.example.backend.data.entity.Teacher;
import com.example.backend.data.entity.ThesisApplication;
import com.example.backend.data.entity.ThesisStatement;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThesisStatementService {

    private final ThesisApplicationRepository thesisApplicationRepository;
    private final ThesisStatementRepository thesisStatementRepository;
    private final ThesisDefenceRepository thesisDefenceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
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
        statement.setGrade(null); // explicitly null
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

        boolean participated = thesisDefenceRepository.existsByStudents_Id(student.getId());

        if (!participated) {
            throw new ConflictException("Student has not participated in a thesis defence");
        }
        //check if the current user is one of the teachers, present in student's thesis defence
        boolean teacherAssigned = thesisDefenceRepository.existsByStudents_IdAndTeachers_Id(student.getId(), teacher.getId());
        if (!teacherAssigned) {
            throw new ConflictException("You are not assigned to this student's defence session");
        }

        ThesisApplication activeApplication = student.getThesisApplications().stream()
                .filter(ThesisApplication::isActive)
                .findFirst()
                .orElseThrow(() -> new ConflictException("Student does not have an active thesis application"));

        ThesisStatement thesisStatement = thesisStatementRepository.findByThesisApplicationId(activeApplication.getId())
                .orElseThrow(() -> new ConflictException("No thesis statement found for the student's active application"));

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
}
