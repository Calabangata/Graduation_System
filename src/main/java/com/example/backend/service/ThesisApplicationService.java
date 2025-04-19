package com.example.backend.service;

import com.example.backend.data.entity.Student;
import com.example.backend.data.entity.Teacher;
import com.example.backend.data.entity.ThesisApplication;
import com.example.backend.data.entity.ThesisApproval;
import com.example.backend.data.repository.StudentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.data.repository.ThesisApplicationRepository;
import com.example.backend.data.repository.ThesisApprovalRepository;
import com.example.backend.dto.request.SubmitThesisApplicationDTO;
import com.example.backend.dto.response.ThesisApplicationResponseDTO;
import com.example.backend.enums.ApprovalStatus;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ThesisApplicationService {

    private final ThesisApplicationRepository thesisApplicationRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ThesisApprovalRepository thesisApprovalRepository;

    /**
     * Submits a thesis application.
     *
     * @param dto the data transfer object containing the application details
     * @return the saved ThesisApplication entity
     */
    @Transactional
    public ThesisApplicationResponseDTO submitApplication(SubmitThesisApplicationDTO dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new UserNotFoundException("Student not found"));

        Teacher supervisor = teacherRepository.findById(dto.getSupervisorId())
                .orElseThrow(() -> new UserNotFoundException("Supervisor not found"));

        if (supervisor.getStudents().contains(student)) {
            throw new UserAlreadyExistsException("Student already has a thesis application with this supervisor");
        } else {
            supervisor.getStudents().add(student);
            student.setTeacher(supervisor);
            teacherRepository.save(supervisor);
            studentRepository.save(student);
        }

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
    }

    private ThesisApplicationResponseDTO toDto(ThesisApplication app) {
        ThesisApplicationResponseDTO dto = new ThesisApplicationResponseDTO();
        dto.setId(app.getId());
        dto.setTopic(app.getTopic());
        dto.setPurpose(app.getPurpose());
        dto.setTasks(app.getTasks());
        dto.setTechStack(app.getTechStack());
        dto.setApproved(app.isApproved());
        dto.setStudentId(app.getStudent().getId());
        dto.setSupervisorId(app.getSupervisor().getId());
        dto.setSupervisorName(app.getSupervisor().getUserInfo().getFirstName() + " " + app.getSupervisor().getUserInfo().getLastName());
        dto.setDepartmentName(app.getSupervisor().getDepartment().getName());
        dto.setApprovalStatus(app.getThesisApproval().getStatus().name());
        return dto;
    }
}
