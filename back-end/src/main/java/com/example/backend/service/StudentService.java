package com.example.backend.service;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.StudentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.dto.UserInfoDTO;
import com.example.backend.dto.response.StudentCountThesisReviewDecisionDTO;
import com.example.backend.dto.response.SupervisorSuccessStatsDTO;
import com.example.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserInfoService userInfoService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public void deactivateStudentAccount(String email) {
        userInfoService.deactivateUser(email);
    }

    public StudentCountThesisReviewDecisionDTO countStudentsByReviewDecision(String status) {
        String decision = status.toUpperCase();
        long count = studentRepository.countStudentsByReviewDecision(decision);
        String msg = String.format("There are %d students with %s thesis review.", count, decision.toLowerCase());

        return new StudentCountThesisReviewDecisionDTO(decision, count, msg);
    }

    public SupervisorSuccessStatsDTO countSuccessfulDefendedBySupervisor(Long supervisorId) {
        if (!teacherRepository.existsById(supervisorId)) {
            throw new ResourceNotFoundException("Supervisor with ID " + supervisorId + " does not exist.");
        }
        long count = studentRepository.countSuccessfulDefencesBySupervisor(supervisorId);
        String msg = String.format("Supervisor with ID %d has %d students who successfully defended.", supervisorId, count);
        return new SupervisorSuccessStatsDTO(count, msg);
    }

    public List<UserInfoDTO> getGraduatedStudentsBetween(LocalDateTime start, LocalDateTime end) {
        return studentRepository.findGraduatedStudentsByDefenceDateRange(start, end).stream()
                .map(student -> {
                    UserInfo info = student.getUserInfo();
                    return new UserInfoDTO(
                            info.getFirstName(),
                            info.getLastName(),
                            info.getEmail(),
                            info.getRole().getName().name()
                    );
                })
                .toList();
    }
}
