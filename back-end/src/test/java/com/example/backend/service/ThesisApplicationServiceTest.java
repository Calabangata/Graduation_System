package com.example.backend.service;

import com.example.backend.data.entity.*;
import com.example.backend.data.repository.*;
import com.example.backend.dto.request.SubmitThesisApplicationDTO;
import com.example.backend.dto.request.VoteOnThesisDTO;
import com.example.backend.dto.response.ThesisApplicationResponseDTO;
import com.example.backend.enums.ApprovalStatus;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.DuplicateActionException;
import com.example.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThesisApplicationServiceTest {
    @Mock private StudentRepository studentRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private TeacherApprovalRepository teacherApprovalRepository;
    @Mock private ThesisApplicationRepository thesisApplicationRepository;
    @Mock private ThesisApprovalRepository thesisApprovalRepository;

    @InjectMocks private ThesisApplicationService service;

    @Test
    void submitApplication_shouldThrow_whenStudentNotFound() {
        SubmitThesisApplicationDTO dto = new SubmitThesisApplicationDTO();
        dto.setStudentId("123");

        when(studentRepository.findById("123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.submitApplication(dto));
    }

    @Test
    void submitApplication_shouldThrow_whenStudentHasActiveApplication() {
        Student student = new Student();
        student.setId("123");
        UserInfo mockUserInfo = new UserInfo();
        mockUserInfo.setEmail("student@example.com");
        student.setUserInfo(mockUserInfo);
        SubmitThesisApplicationDTO dto = new SubmitThesisApplicationDTO();
        dto.setStudentId("123");

        when(studentRepository.findById("123")).thenReturn(Optional.of(student));
        when(thesisApplicationRepository.existsByStudentIdAndActiveTrue("123")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.submitApplication(dto));
        assertEquals("Student already has an active thesis application", ex.getMessage());
    }

    @Test
    void submitApplication_shouldThrow_whenSupervisorNotFound() {
        SubmitThesisApplicationDTO dto = new SubmitThesisApplicationDTO();
        dto.setStudentId("123");

        Student student = new Student();
        UserInfo mockUserInfo = new UserInfo();
        mockUserInfo.setEmail("student@example.com");
        student.setUserInfo(mockUserInfo);
        student.setId("123");

        when(studentRepository.findById("123")).thenReturn(Optional.of(student));
        when(thesisApplicationRepository.existsByStudentIdAndActiveTrue("123")).thenReturn(false);
        when(teacherRepository.findByUserInfo_Email(anyString())).thenReturn(Optional.empty());

        setSecurityContext("teacher@example.com");

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.submitApplication(dto));
        assertEquals("Reviewer not found or not owned by current user", ex.getMessage());
    }

    @Test
    void submitApplication_shouldSucceed_whenValid() {
        SubmitThesisApplicationDTO dto = new SubmitThesisApplicationDTO();
        dto.setStudentId("123");
        dto.setTopic("Topic");
        dto.setPurpose("Purpose");
        dto.setTasks("Tasks");
        dto.setTechStack("Stack");

        Student student = new Student();
        student.setId("123");
        UserInfo mockUserInfo = new UserInfo();
        mockUserInfo.setEmail("student@example.com");
        student.setUserInfo(mockUserInfo);

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUserInfo(new UserInfo());
        teacher.getUserInfo().setEmail("teacher@example.com");
        teacher.setStudents(new ArrayList<>());
        Department department = new Department();
        teacher.setDepartment(department);

        when(studentRepository.findById("123")).thenReturn(Optional.of(student));
        when(thesisApplicationRepository.existsByStudentIdAndActiveTrue("123")).thenReturn(false);
        when(teacherRepository.findByUserInfo_Email("teacher@example.com")).thenReturn(Optional.of(teacher));
        when(thesisApprovalRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(thesisApplicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        setSecurityContext("teacher@example.com");

        ThesisApplicationResponseDTO response = service.submitApplication(dto);
        assertNotNull(response);
        assertEquals("Topic", response.getTopic());
        assertEquals("Purpose", response.getPurpose());
        assertEquals("Tasks", response.getTasks());
        assertEquals("Stack", response.getTechStack());
    }

    @Test
    void submitApplication_shouldThrow_whenSupervisorHasNoDepartment() {
        SubmitThesisApplicationDTO dto = new SubmitThesisApplicationDTO();
        dto.setStudentId("123");

        Student student = new Student();
        student.setId("123");
        UserInfo info = new UserInfo();
        info.setEmail("student@example.com");
        student.setUserInfo(info);

        Teacher supervisor = new Teacher(); // no department
        supervisor.setId(1L);
        supervisor.setUserInfo(new UserInfo());
        supervisor.getUserInfo().setEmail("teacher@example.com");
        supervisor.setStudents(new ArrayList<>());

        when(studentRepository.findById("123")).thenReturn(Optional.of(student));
        when(thesisApplicationRepository.existsByStudentIdAndActiveTrue("123")).thenReturn(false);
        when(teacherRepository.findByUserInfo_Email("teacher@example.com")).thenReturn(Optional.of(supervisor));

        setSecurityContext("teacher@example.com");

        ConflictException ex = assertThrows(ConflictException.class, () ->
                service.submitApplication(dto)
        );
        assertEquals("The Current supervisor must belong to a department to complete this action.", ex.getMessage());
    }

    @Test
    void vote_shouldThrow_whenTeacherAlreadyVoted() {
        setSecurityContext("teacher@example.com");

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("teacher@example.com");
        teacher.setUserInfo(userInfo);
        Department dept = new Department();
        dept.setId(1L);
        teacher.setDepartment(dept);

        TeacherApproval approval = new TeacherApproval();
        approval.setTeacher(teacher);

        ThesisApproval thesisApproval = new ThesisApproval();
        thesisApproval.setTeacherApprovals(List.of(approval));
        thesisApproval.setDepartment(dept);

        ThesisApplication app = new ThesisApplication();
        app.setId(1L);
        app.setThesisApproval(thesisApproval);

        when(thesisApplicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(teacherRepository.findByUserInfo_Email("teacher@example.com")).thenReturn(Optional.of(teacher));

       DuplicateActionException ex = assertThrows(DuplicateActionException.class, () ->
                service.voteOnThesis(new VoteOnThesisDTO(1L, true))
        );
       assertEquals("Teacher has already voted.", ex.getMessage());
    }


    @Test
    void evaluateVotes_shouldApprove_whenMajorityPositive() {
        setSecurityContext("teacher@example.com");

        ThesisApproval approval = new ThesisApproval();
        Department department = new Department();
        department.setTeachers(new ArrayList<>());
        department.setId(1L);

        approval.setDepartment(department);

        approval.setTeacherApprovals(new ArrayList<>());
        approval.setStatus(ApprovalStatus.PENDING);

        TeacherApproval vote1 = new TeacherApproval();
        vote1.setApprovalStatus(ApprovalStatus.APPROVED);

        TeacherApproval vote2 = new TeacherApproval();
        vote2.setApprovalStatus(ApprovalStatus.APPROVED);

        TeacherApproval vote3 = new TeacherApproval();
        vote3.setApprovalStatus(ApprovalStatus.REJECTED);

        approval.getTeacherApprovals().addAll(List.of(vote1, vote2, vote3));

        ThesisApplication application = new ThesisApplication();
        application.setId(1L);
        application.setThesisApproval(approval);

        when(thesisApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        service.evaluateVotes(1L);

        assertEquals(ApprovalStatus.APPROVED, approval.getStatus());
    }

    @Test
    void evaluateVotes_shouldReject_whenMajorityNegative() {

        setSecurityContext("teacher@example.com");

        ThesisApproval approval = new ThesisApproval();
        Department department = new Department();
        department.setTeachers(new ArrayList<>());
        department.setId(1L);

        approval.setDepartment(department);

        approval.setTeacherApprovals(new ArrayList<>());
        approval.setStatus(ApprovalStatus.PENDING);

        TeacherApproval vote1 = new TeacherApproval();
        vote1.setApprovalStatus(ApprovalStatus.REJECTED);

        TeacherApproval vote2 = new TeacherApproval();
        vote2.setApprovalStatus(ApprovalStatus.REJECTED);

        TeacherApproval vote3 = new TeacherApproval();
        vote3.setApprovalStatus(ApprovalStatus.APPROVED);

        approval.getTeacherApprovals().addAll(List.of(vote1, vote2, vote3));

        ThesisApplication application = new ThesisApplication();
        application.setId(1L);
        application.setThesisApproval(approval);
        approval.setThesisApplication(application);

        when(thesisApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        service.evaluateVotes(1L);

        assertEquals(ApprovalStatus.REJECTED, approval.getStatus());
    }

    @Test
    void evaluateVotes_shouldThrowConflict_whenNotAllTeachersVoted() {
        Department department = new Department();
        department.setId(1L);

        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setDepartment(department);

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setDepartment(department);

        department.setTeachers(List.of(teacher1, teacher2)); // Two teachers expected to vote

        TeacherApproval approval1 = new TeacherApproval();
        approval1.setTeacher(teacher1);
        approval1.setApprovalStatus(ApprovalStatus.APPROVED); // Only one has voted

        ThesisApproval thesisApproval = new ThesisApproval();
        thesisApproval.setStatus(ApprovalStatus.PENDING);
        thesisApproval.setTeacherApprovals(List.of(approval1));
        thesisApproval.setDepartment(department);

        ThesisApplication application = new ThesisApplication();
        application.setId(1L);
        application.setThesisApproval(thesisApproval);

        when(thesisApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(ConflictException.class, () -> service.evaluateVotes(1L));

        assertEquals("Not all teachers in the department have voted yet.", exception.getMessage());
    }

    @Test
    void deleteApplication_shouldDeleteAllLinkedEntities_whenNoThesisStatementExists() {
        Long appId = 1L;

        // Create mock entities
        ThesisApplication application = new ThesisApplication();
        application.setId(appId);
        application.setThesisStatement(null); // no statement

        ThesisApproval approval = new ThesisApproval();
        approval.setId(2L);

        TeacherApproval ta1 = new TeacherApproval();
        ta1.setId(10L);
        ta1.setThesisApproval(approval);

        TeacherApproval ta2 = new TeacherApproval();
        ta2.setId(11L);
        ta2.setThesisApproval(approval);

        approval.setTeacherApprovals(List.of(ta1, ta2));
        application.setThesisApproval(approval);

        when(thesisApplicationRepository.findById(appId)).thenReturn(Optional.of(application));

        // Execute
        service.deleteApplication(appId);

        // Verify all deletions
        verify(teacherApprovalRepository).delete(ta1);
        verify(teacherApprovalRepository).delete(ta2);
        verify(thesisApprovalRepository).delete(approval);
        verify(thesisApplicationRepository).delete(application);
    }

    @Test
    void deleteApplication_shouldThrow_whenThesisStatementExists() {
        Long applicationId = 1L;
        ThesisApplication application = new ThesisApplication();
        application.setId(applicationId);
        application.setThesisStatement(new ThesisStatement()); // simulate attached statement

        when(thesisApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(ConflictException.class, () -> service.deleteApplication(applicationId));

        assertEquals("Cannot delete application with an associated thesis statement.", exception.getMessage());
        verify(thesisApplicationRepository, never()).delete(any());
    }

    @Test
    void getAllThesisApplications_shouldReturnEmptyList_whenNoApplications() {
        when(thesisApplicationRepository.findAll()).thenReturn(List.of());
        List<ThesisApplicationResponseDTO> result = service.getAllThesisApplications();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllThesisApplicationsByStudentId_shouldReturnMappedDTOs() {
        String studentId = "123";
        Student student = new Student();
        student.setId(studentId);

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUserInfo(new UserInfo());
        teacher.setDepartment(new Department());

        ThesisApproval approval = new ThesisApproval();
        approval.setStatus(ApprovalStatus.APPROVED);

        ThesisApplication app = new ThesisApplication();
        app.setId(1L);
        app.setTopic("Topic");
        app.setPurpose("Purpose");
        app.setTasks("Tasks");
        app.setTechStack("Tech");
        app.setThesisApproval(approval);
        app.setStudent(student);
        app.setSupervisor(teacher);

        student.setThesisApplications(List.of(app));

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        var result = service.getAllThesisApplicationsByStudentId(studentId);

        assertEquals(1, result.size());
        assertEquals("Topic", result.getFirst().getTopic());
    }


    @Test
    void getAllThesisApplicationsByStudentId_shouldThrow_whenStudentNotFound() {
        String studentId = "nonexistent";
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.getAllThesisApplicationsByStudentId(studentId));

        assertEquals("Student not found", ex.getMessage());
        verify(studentRepository).findById(studentId);
    }

    @Test
    void searchByTopic_shouldReturnMatchingResults() {
        ThesisApplication app = new ThesisApplication();
        app.setTopic("AI Thesis");
        app.setPurpose("purpose");
        app.setTasks("tasks");
        app.setTechStack("tech");
        app.setThesisApproval(new ThesisApproval());
        app.getThesisApproval().setStatus(ApprovalStatus.APPROVED);

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUserInfo(new UserInfo());
        teacher.setDepartment(new Department());

        app.setSupervisor(teacher);

        when(thesisApplicationRepository.findAllByTopicContaining("AI")).thenReturn(List.of(app));

        List<ThesisApplicationResponseDTO> result = service.searchByTopic("AI");

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getTopic().contains("AI"));
    }



    private void setSecurityContext(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of())
        );
    }


}
