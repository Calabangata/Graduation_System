package com.example.backend.service;

import com.example.backend.data.entity.*;
import com.example.backend.data.repository.*;
import com.example.backend.dto.request.SubmitThesisApplicationDTO;
import com.example.backend.dto.request.VoteOnThesisDTO;
import com.example.backend.dto.response.ThesisApplicationResponseDTO;
import com.example.backend.enums.ApprovalStatus;
import com.example.backend.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ThesisApplicationService {

    private final ThesisApplicationRepository thesisApplicationRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ThesisApprovalRepository thesisApprovalRepository;
    private final TeacherApprovalRepository teacherApprovalRepository;

    /**
     * Submits a thesis application.
     *
     * @param dto the data transfer object containing the application details
     * @return the saved ThesisApplication entity
     */
    @Transactional
    public ThesisApplicationResponseDTO submitApplication(SubmitThesisApplicationDTO dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.isGraduated()) {
            throw new ConflictException("Student has already graduated and cannot submit a thesis application.");
        }

        //TODO: Remove this, its for debugging
        ThesisApplication existingApplication = thesisApplicationRepository
                .findByStudent_UserInfo_EmailAndActiveTrue(student.getUserInfo().getEmail())
                .orElse(null);

        boolean hasActiveApplication = thesisApplicationRepository.existsByStudentIdAndActiveTrue(student.getId());
        if (hasActiveApplication) {
            throw new ConflictException("Student already has an active thesis application");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Teacher supervisor = teacherRepository
                .findByUserInfo_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found or not owned by current user"));
        if (supervisor.getDepartment() != null) {

            supervisor.getStudents().add(student);
            student.setTeacher(supervisor);
            teacherRepository.save(supervisor);
            studentRepository.save(student);

            ThesisApproval approval = new ThesisApproval();
            approval.setDepartment(supervisor.getDepartment());
            approval.setStatus(ApprovalStatus.PENDING);
            thesisApprovalRepository.save(approval);

            ThesisApplication application = new ThesisApplication();
            application.setTopic(dto.getTopic());
            application.setPurpose(dto.getPurpose());
            application.setTasks(dto.getTasks());
            application.setTechStack(dto.getTechStack());
            application.setStudent(student);
            application.setSupervisor(supervisor);
            application.setThesisApproval(approval);

            return toDto(thesisApplicationRepository.save(application));
        } else {
            throw new ConflictException("The Current supervisor must belong to a department to complete this action.");
        }
    }

    public void voteOnThesis(VoteOnThesisDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository
                .findByUserInfo_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found or not owned by current user"));

        ThesisApplication application = thesisApplicationRepository.findById(dto.getThesisApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Thesis application not found"));

        ThesisApproval approval = application.getThesisApproval();

        if (!approval.getDepartment().getId().equals(teacher.getDepartment().getId())) {
            throw new ForbiddenActionException("Teacher does not belong to the required department.");
        }

        if (approval.getTeacherApprovals() != null && approval.getTeacherApprovals().stream()
                .anyMatch(a -> a.getTeacher().getId().equals(teacher.getId()))) {
            throw new DuplicateActionException("Teacher has already voted.");
        }

        int totalTeachers = approval.getDepartment().getTeachers().size();
        if (approval.getTeacherApprovals() != null && approval.getTeacherApprovals().size() >= totalTeachers) {
            throw new ConflictException("All teachers in the department have already voted.");
        }

        TeacherApproval teacherApproval = new TeacherApproval();
        teacherApproval.setTeacher(teacher);
        teacherApproval.setThesisApproval(approval);
        teacherApproval.setApprovalStatus(dto.isApproved() ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        teacherApprovalRepository.save(teacherApproval);
    }

    private ThesisApplicationResponseDTO toDto(ThesisApplication app) {
        ThesisApplicationResponseDTO dto = new ThesisApplicationResponseDTO();
        dto.setId(app.getId());
        dto.setTopic(app.getTopic());
        dto.setPurpose(app.getPurpose());
        dto.setTasks(app.getTasks());
        dto.setTechStack(app.getTechStack());
        dto.setApproved(app.getThesisApproval().getStatus() == ApprovalStatus.APPROVED);
        dto.setStudentId(app.getStudent().getId());
        dto.setSupervisorId(app.getSupervisor().getId());
        dto.setSupervisorName(app.getSupervisor().getUserInfo().getFirstName() + " " + app.getSupervisor().getUserInfo().getLastName());
        dto.setDepartmentName(app.getSupervisor().getDepartment().getName());
        dto.setApprovalStatus(app.getThesisApproval().getStatus().name());
        return dto;
    }

    public void evaluateVotes(Long applicationId) {
        ThesisApplication application = thesisApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis application not found"));
        ThesisApproval approval = application.getThesisApproval();
        if (approval.getTeacherApprovals().size() < approval.getDepartment().getTeachers().size()) {
            throw new ConflictException("Not all teachers in the department have voted yet.");
        }
        evaluateApprovalStatus(approval);
    }

    private void evaluateApprovalStatus(ThesisApproval approval) {
        long totalVotes = approval.getTeacherApprovals().size();
        long positiveVotes = approval.getTeacherApprovals().stream()
                .filter(a -> a.getApprovalStatus() == ApprovalStatus.APPROVED)
                .count();
        long negativeVotes = approval.getTeacherApprovals().stream()
                .filter(a -> a.getApprovalStatus() == ApprovalStatus.REJECTED)
                .count();

        if (positiveVotes >= totalVotes / 2) {
            approval.setStatus(ApprovalStatus.APPROVED);
            thesisApprovalRepository.save(approval);
        } else if (negativeVotes > totalVotes / 2) {
            approval.setStatus(ApprovalStatus.REJECTED);
            thesisApprovalRepository.save(approval);
        }
    }

    @Transactional
    public void deleteApplication(Long applicationId) {

        ThesisApplication application = thesisApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis application not found"));
        if (application.getThesisStatement() != null) {
            throw new ConflictException("Cannot delete application with an associated thesis statement.");
        }
        if (application.getThesisApproval() != null) {
            ThesisApproval approval = application.getThesisApproval();
            if (approval.getTeacherApprovals() != null) {
                for (TeacherApproval teacherApproval : approval.getTeacherApprovals()) {
                    teacherApprovalRepository.delete(teacherApproval);
                }
            }
            thesisApprovalRepository.delete(approval);
        }
        thesisApplicationRepository.delete(application);
    }

    public List<ThesisApplicationResponseDTO> getAllThesisApplications() {
        //Find all thesis applications
        return thesisApplicationRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public List<ThesisApplicationResponseDTO> getAllThesisApplicationsByStudentId(String studentId) {
        //Find all thesis applications by student ID
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return student.getThesisApplications().stream()
                .map(this::toDto)
                .toList();

    }

    public List<ThesisApplicationResponseDTO> getThesisApplicationsBySupervisorId(Long supervisorId, String approvalStatus) {
        //Find all thesis applications by supervisor ID and approval status
        Teacher supervisor = teacherRepository.findById(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found"));

        //TODO: add validation for approval status
        ApprovalStatus status = ApprovalStatus.valueOf(approvalStatus.toUpperCase());
        return thesisApplicationRepository.findAllByThesisApproval_StatusAndSupervisor_Id(status, supervisor.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    public List<ThesisApplicationResponseDTO> searchByTopic(String keyword) {
        // Find all thesis applications by topic
        return thesisApplicationRepository.findAllByTopicContaining(keyword).stream()
                .map(this::toDto)
                .toList();
    }
}
