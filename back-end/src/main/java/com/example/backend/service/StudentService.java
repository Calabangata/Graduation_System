package com.example.backend.service;

import com.example.backend.data.entity.Student;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.StudentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.UserInfoDTO;
import com.example.backend.dto.response.StudentCountThesisReviewDecisionDTO;
import com.example.backend.dto.response.SupervisorSuccessStatsDTO;
import com.example.backend.enums.UserRole;
import com.example.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserInfoService userInfoService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final UserInfoRepository userInfoRepository;

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

    /**
     * Get total student count based on user role.
     * - ADMIN/SUPER_ADMIN: Returns total count of all students
     * - TEACHER: Returns count of students supervised by this teacher
     */
    public long getTotalStudentCount() {
        return getStudentsForCurrentUser().size();
    }

    /**
     * Get student list based on user role.
     * - ADMIN/SUPER_ADMIN: Returns all students
     * - TEACHER: Returns students supervised by this teacher
     */
    public List<UserInfoDTO> getStudentList() {
        return getStudentsForCurrentUser().stream()
                .map(this::convertStudentToDTO)
                .toList();
    }

    /**
     * Helper method: Get student list for current user based on their role.
     * Eliminates code duplication between getTotalStudentCount and getStudentList.
     */
    private List<Student> getStudentsForCurrentUser() {
        UserInfo currentUser = getCurrentUserInfo();
        UserRole role = currentUser.getRole().getName();

        if (role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN) {
            return studentRepository.findAll();
        } else if (role == UserRole.TEACHER) {
            var teacher = currentUser.getTeacher();
            return teacher != null 
                    ? studentRepository.findByTeacherId(teacher.getId())
                    : List.of();
        }
        return List.of();
    }

    /**
     * Helper method: Convert Student entity to UserInfoDTO
     */
    private UserInfoDTO convertStudentToDTO(Student student) {
        UserInfo info = student.getUserInfo();
        return new UserInfoDTO(
                info.getFirstName(),
                info.getLastName(),
                info.getEmail(),
                info.getRole().getName().name()
        );
    }

    /**
     * Get current authenticated user info from security context
     */
    private UserInfo getCurrentUserInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
