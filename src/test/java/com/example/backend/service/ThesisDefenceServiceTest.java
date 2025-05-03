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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThesisDefenceServiceTest {

    @Mock
    private ThesisDefenceRepository thesisDefenceRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private ThesisDefenceService thesisDefenceService;



    @Test
    void testCreateDefence_ThrowsConflictException_IfDateIsInPast() {
        CreateThesisDefenceRequestDTO request = new CreateThesisDefenceRequestDTO();
        request.setDate(LocalDateTime.now().minusDays(1)); // invalid past date
        ConflictException ex = assertThrows(ConflictException.class, () ->
                thesisDefenceService.createDefence(request)
        );
        assertEquals("Scheduled date and time must be in the future.", ex.getMessage());
    }

    @Test
    void testCreateDefence_ThrowsIfDepartmentNotFound() {
        CreateThesisDefenceRequestDTO request = new CreateThesisDefenceRequestDTO();
        request.setDate(LocalDateTime.now().plusDays(1));
        request.setDepartmentId(99L); // simulate non-existing
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                thesisDefenceService.createDefence(request)
        );
        assertEquals("Department not found.", ex.getMessage());
    }



    @Test
    void createDefence_NoAddedStudents_ShouldReturnDTO() {
        Department dept = new Department();
        dept.setId(1L);

        CreateThesisDefenceRequestDTO request = new CreateThesisDefenceRequestDTO();
        request.setDate(LocalDateTime.now().plusDays(1));
        request.setDepartmentId(1L);

        ThesisDefence saved = new ThesisDefence();
        saved.setId(1L);
        saved.setStudents(new ArrayList<>());
        saved.setTeachers(new ArrayList<>());

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(thesisDefenceRepository.save(any())).thenReturn(saved);

        ThesisDefenceResponseDTO result = thesisDefenceService.createDefence(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Defence scheduled with 0 students.", result.getScheduledMessage());
    }

    @Test
    void createDefence_shouldAssignEligibleStudentAndTeacher() {
        // Prepare DTO
        CreateThesisDefenceRequestDTO request = new CreateThesisDefenceRequestDTO();
        request.setDate(LocalDateTime.now().plusDays(2));
        request.setLocation("Room 101");
        request.setDepartmentId(1L);
        request.setStudentIds(List.of("s1"));
        request.setTeacherIds(List.of(1L));

        // Department
        Department dept = new Department();
        dept.setId(1L);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));

        // Student
        UserInfo studentInfo = new UserInfo();
        studentInfo.setFirstName("Jane");
        studentInfo.setLastName("Doe");
        Student student = new Student();
        student.setId("s1");
        student.setGraduated(false);
        student.setUserInfo(studentInfo);

        ThesisApplication app = new ThesisApplication();
        ThesisReview review = new ThesisReview();
        review.setApprovalDecision("APPROVED");
        ThesisStatement statement = new ThesisStatement();
        statement.setThesisReview(review);
        app.setThesisStatement(statement);
        app.setActive(true);
        student.setThesisApplications(List.of(app));

        when(studentRepository.findAllById(List.of("s1"))).thenReturn(List.of(student));

        // Teacher
        UserInfo teacherInfo = new UserInfo();
        teacherInfo.setFirstName("John");
        teacherInfo.setLastName("Smith");
        teacherInfo.setEmail("john@example.com");

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setDepartment(dept);
        teacher.setUserInfo(teacherInfo);

        when(teacherRepository.findAllById(List.of(1L))).thenReturn(List.of(teacher));

        // Save returns same entity
        when(thesisDefenceRepository.save(any())).thenAnswer(invocation -> {
            ThesisDefence d = invocation.getArgument(0);
            d.setId(42L);
            return d;
        });

        ThesisDefenceResponseDTO result = thesisDefenceService.createDefence(request);

        assertNotNull(result);
        assertEquals("Defence scheduled with 1 students.", result.getScheduledMessage());
        assertEquals(1, result.getStudents().size());
        assertEquals(1, result.getTeachers().size());
    }

    @Test
    void assignTeachers_shouldAssignEligibleTeachersSuccessfully() {
        // given
        Long defenceId = 1L;
        String email = "teacher@example.com";
        List<String> emails = List.of(email);

        UserInfo info = new UserInfo();
        info.setEmail(email);
        info.setFirstName("Test");
        info.setLastName("User");

        Department dept = new Department();
        dept.setId(10L);

        Teacher teacher = new Teacher();
        teacher.setUserInfo(info);
        teacher.setDepartment(dept);

        ThesisDefence defence = new ThesisDefence();
        defence.setId(defenceId);
        defence.setDepartment(dept);
        defence.setTeachers(new ArrayList<>());

        when(thesisDefenceRepository.findById(defenceId)).thenReturn(Optional.of(defence));
        when(teacherRepository.findAllByUserInfo_EmailIn(emails)).thenReturn(List.of(teacher));
        when(thesisDefenceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        ThesisDefenceResponseDTO result = thesisDefenceService.assignTeachers(defenceId, emails);

        // then
        assertNotNull(result);
        assertTrue(result.getTeachers().containsKey(email));
        assertEquals("Defence scheduled with 0 students.", result.getScheduledMessage());

        verify(thesisDefenceRepository).save(defence);
    }


    @Test
    void isStudentEligibleForDefence_shouldReturnTrue_whenValid() throws Exception {
        // Setup eligible student with approved thesis statement
        Student student = new Student();
        student.setId("s123");
        student.setGraduated(false);

        ThesisReview review = new ThesisReview();
        review.setApprovalDecision("APPROVED");

        ThesisStatement statement = new ThesisStatement();
        statement.setThesisReview(review);

        ThesisApplication application = new ThesisApplication();
        application.setThesisStatement(statement);
        application.setActive(true);
        student.setThesisApplications(List.of(application));

        when(thesisDefenceRepository.existsByStudents_Id("s123")).thenReturn(false);

        Method method = ThesisDefenceService.class.getDeclaredMethod("isStudentEligibleForDefence", Student.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(thesisDefenceService, student);

        assertTrue(result);
    }

    @Test
    void isEligibleTeacher_shouldReturnFalse_whenDepartmentMismatch() throws Exception {
        Department dep1 = new Department();
        dep1.setId(1L);
        Department dep2 = new Department();
        dep2.setId(2L);

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setDepartment(dep1);

        ThesisDefence defence = new ThesisDefence();
        defence.setDepartment(dep2);

        Method method = ThesisDefenceService.class.getDeclaredMethod("isEligibleTeacher", Teacher.class, ThesisDefence.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(thesisDefenceService, teacher, defence);

        assertFalse(result);
    }



    @Test
    void assignStudents_ThrowsIfDefenceNotFound() {
        when(thesisDefenceRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                thesisDefenceService.assignStudents(1L, new ArrayList<>()));
        assertEquals("Thesis Defence not found", ex.getMessage());
    }

    @Test
    void assignStudents_ThrowsIfStudentNotFound() {
        ThesisDefence defence = new ThesisDefence();
        defence.setId(1L);

        Student student = new Student();
        student.setId("student1");

        List<String> expectedStudents = List.of("student1", "student2");
        List<Student> foundStudents = List.of(student);

        when(thesisDefenceRepository.findById(1L)).thenReturn(Optional.of(defence));
        when(studentRepository.findAllByIdIn(any())).thenReturn(foundStudents);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                thesisDefenceService.assignStudents(1L, expectedStudents));
        assertEquals("One or more students not found for provided faculty numbers.", ex.getMessage());
    }



    @Test
    void deleteDefence_shouldClearAssociationsAndDelete() {
        var defence = new ThesisDefence();
        defence.setId(1L);
        defence.setStudents(new ArrayList<>());
        defence.setTeachers(new ArrayList<>());
        when(thesisDefenceRepository.findById(1L)).thenReturn(Optional.of(defence));
        thesisDefenceService.deleteDefence(1L);
        verify(thesisDefenceRepository).save(defence);
        verify(thesisDefenceRepository).delete(defence);
    }

    @Test
    void testUpdateDefence_ThrowsIfNotFound() {
        when(thesisDefenceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                thesisDefenceService.updateDefence(99L, new UpdateThesisDefenceRequestDTO()));
        assertEquals("Thesis defence not found.", ex.getMessage());
    }

    @Test
    void testUpdateDefence_ThrowsIfPastDateGiven() {
        var defence = new ThesisDefence();
        when(thesisDefenceRepository.findById(1L)).thenReturn(Optional.of(defence));

        var request = new UpdateThesisDefenceRequestDTO();
        request.setDate(LocalDateTime.now().minusDays(2));

        ConflictException ex = assertThrows(ConflictException.class, () ->
                thesisDefenceService.updateDefence(1L, request));

        assertEquals("Scheduled date and time must be in the future.", ex.getMessage());
    }

    @Test
    void testUpdateDefence_SuccessfullyUpdatesDateAndLocation() {
        ThesisDefence defence = new ThesisDefence();
        defence.setId(1L);
        defence.setDate(LocalDateTime.now().plusDays(3));
        defence.setLocation("Old");
        when(thesisDefenceRepository.findById(1L)).thenReturn(Optional.of(defence));
        when(thesisDefenceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateThesisDefenceRequestDTO request = new UpdateThesisDefenceRequestDTO();
        request.setDate(LocalDateTime.now().plusDays(5));
        request.setLocation("New");
        ThesisDefenceResponseDTO result = thesisDefenceService.updateDefence(1L, request);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Defence scheduled with 0 students.", result.getScheduledMessage());
        assertTrue(result.getStudents().isEmpty());
        assertTrue(result.getTeachers().isEmpty());
        verify(thesisDefenceRepository).save(defence);
    }

}
