package com.example.backend.service;

import com.example.backend.data.entity.*;
import com.example.backend.data.repository.DepartmentRepository;
import com.example.backend.data.repository.StudentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.data.repository.ThesisDefenceRepository;
import com.example.backend.dto.request.CreateThesisDefenceRequestDTO;
import com.example.backend.dto.request.UpdateThesisDefenceRequestDTO;
import com.example.backend.dto.response.ThesisDefenceResponseDTO;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThesisDefenceService {

    private final ThesisDefenceRepository thesisDefenceRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private static final Logger log = LoggerFactory.getLogger(ThesisDefenceService.class);

    @Transactional
    public ThesisDefenceResponseDTO createDefence(CreateThesisDefenceRequestDTO requestDTO) {
        if (requestDTO.getDate() == null || requestDTO.getDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Scheduled date and time must be in the future.");
        }

        Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found."));

        ThesisDefence defence = new ThesisDefence();
        defence.setDate(requestDTO.getDate());
        defence.setLocation(requestDTO.getLocation());
        defence.setDepartment(department);

        if (requestDTO.getStudentIds() != null && !requestDTO.getStudentIds().isEmpty()) {
            List<Student> students = studentRepository.findAllById(requestDTO.getStudentIds())
                    .stream()
                    .filter(this::isStudentEligibleForDefence)
                    .collect(Collectors.toList());
            defence.setStudents(students);
        }

        if (requestDTO.getTeacherIds() != null && !requestDTO.getTeacherIds().isEmpty()) {
            List<Teacher> teachers = teacherRepository.findAllById(requestDTO.getTeacherIds())
                    .stream()
                    .filter(t -> isEligibleTeacher(t, defence))
                    .collect(Collectors.toList());
            defence.setTeachers(teachers);
        }

        return toDto(thesisDefenceRepository.save(defence));
    }

    @Transactional
    public ThesisDefenceResponseDTO assignStudents(Long defenceId, List<String> facultyNumbers) {
        ThesisDefence defence = thesisDefenceRepository.findById(defenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis Defence not found"));

        List<Student> students = studentRepository.findAllByIdIn(facultyNumbers);

        if (students.size() != facultyNumbers.size()) {
            throw new ResourceNotFoundException("One or more students not found for provided faculty numbers.");
        }

        List<Student> eligibleStudents = students.stream()
                .filter(this::isStudentEligibleForDefence)
                .toList();

        if (eligibleStudents.isEmpty()) {
            throw new ConflictException("No eligible students to assign for defence.");
        }
        defence.getStudents().addAll(eligibleStudents);
        ThesisDefence saved = thesisDefenceRepository.save(defence);
        return toDto(saved);
    }

    @Transactional
    public ThesisDefenceResponseDTO assignTeachers(Long defenceId, List<String> teacherEmails) {
        ThesisDefence defence = thesisDefenceRepository.findById(defenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis Defence not found"));

        List<Teacher> teachers = teacherRepository.findAllByUserInfo_EmailIn(teacherEmails);

        if (teachers.size() != teacherEmails.size()) {
            throw new ResourceNotFoundException("One or more teachers not found for provided emails.");
        }

        List<Teacher> eligibleTeachers = teachers.stream()
                .filter(teacher -> isEligibleTeacher(teacher, defence))
                .toList();

        if (eligibleTeachers.isEmpty()) {
            throw new ConflictException("No eligible teachers to assign for defence.");
        }

        defence.getTeachers().addAll(eligibleTeachers);
        ThesisDefence saved = thesisDefenceRepository.save(defence);

        return toDto(saved);
    }

    @Transactional
    public ThesisDefenceResponseDTO updateDefence(Long id, UpdateThesisDefenceRequestDTO requestDTO) {
        ThesisDefence defence = thesisDefenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis defence not found."));

        if (requestDTO.getDate() != null && requestDTO.getDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Scheduled date and time must be in the future.");
        }

        if (requestDTO.getDate() != null) {
            defence.setDate(requestDTO.getDate());
        }

        if (requestDTO.getLocation() != null) {
            defence.setLocation(requestDTO.getLocation());
        }

        ThesisDefence updated = thesisDefenceRepository.save(defence);
        log.info("Thesis defence with ID [{}] updated successfully.", id);
        return toDto(updated);
    }


    @Transactional
    public void deleteDefence(Long id) {
        ThesisDefence defence = thesisDefenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis defence not found"));

        defence.getStudents().clear();
        defence.getTeachers().clear();
        thesisDefenceRepository.save(defence);

        thesisDefenceRepository.delete(defence);
        log.info("Thesis defence with ID [{}] deleted successfully.", id);
    }

    private boolean isStudentEligibleForDefence(Student student) {
        if (student.isGraduated()) {
            log.warn("Student [{}] has already graduated. Skipped.", student.getId());
            return false;
        }

        //check if the student is already assigned to a defence
        if (thesisDefenceRepository.existsByStudents_Id(student.getId())) {
            log.warn("Student [{}] is already assigned to a defence. Skipped.", student.getId());
            return false;
        }

        ThesisApplication latestApplication = student.getThesisApplications().stream()
                .filter(ThesisApplication::isActive)
                .findFirst()
                .orElse(null);

        ThesisStatement thesisStatement = latestApplication != null ? latestApplication.getThesisStatement() : null;

        if (thesisStatement != null && thesisStatement.getThesisReview() != null) {
            return "APPROVED".equals(thesisStatement.getThesisReview().getApprovalDecision());
        } else {
            log.warn("Student [{}] is not eligible for defence (missing/invalid review). Skipped.", student.getId());
            return false;
        }
    }

    private boolean isEligibleTeacher(Teacher teacher, ThesisDefence defence) {
        if (teacher.getDepartment() == null) {
            log.warn("Teacher [{}] has no department assigned. Skipped.", teacher.getId());
            return false;
        }
        if (!teacher.getDepartment().getId().equals(defence.getDepartment().getId())) {
            log.warn("Teacher [{}] does not belong to the same department as the defence. Skipped.", teacher.getId());
            return false;
        }
        return true;
    }

    private ThesisDefenceResponseDTO toDto(ThesisDefence defence) {
        Map<String, String> students = defence.getStudents() != null ? defence.getStudents().stream()
                .collect(Collectors.toMap(
                        Student::getId,
                        s -> s.getUserInfo().getFirstName() + " " + s.getUserInfo().getLastName()
                )) : Map.of();

        Map<String, String> teachers = defence.getTeachers() != null ? defence.getTeachers().stream()
                .collect(Collectors.toMap(
                        t -> t.getUserInfo().getEmail(),
                        t -> t.getUserInfo().getFirstName() + " " + t.getUserInfo().getLastName()
                )) : Map.of();

        ThesisDefenceResponseDTO dto = new ThesisDefenceResponseDTO();
        dto.setId(defence.getId());
        dto.setScheduledMessage("Defence scheduled with " + students.size() + " students.");
        dto.setStudents(students);
        dto.setTeachers(teachers);
        return dto;
    }
}
