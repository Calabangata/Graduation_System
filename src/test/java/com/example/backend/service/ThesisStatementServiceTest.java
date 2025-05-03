package com.example.backend.service;

import com.example.backend.data.entity.*;
import com.example.backend.data.repository.*;
import com.example.backend.dto.ThesisStatementDTO;
import com.example.backend.dto.request.GradeThesisDTO;
import com.example.backend.enums.ApprovalStatus;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ForbiddenActionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThesisStatementServiceTest {

    @Mock
    private ThesisApplicationRepository applicationRepo;
    @Mock
    private ThesisStatementRepository statementRepo;
    @Mock
    private ThesisDefenceRepository defenceRepo;
    @Mock
    private StudentRepository studentRepo;
    @Mock
    private TeacherRepository teacherRepo;

    @InjectMocks
    private ThesisStatementService service;

    private void setSecurityContext(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of())
        );
    }

    @Test
    void create_shouldSucceed() {
        setSecurityContext("student@mail.com");

        ThesisApplication app = new ThesisApplication();
        app.setThesisApproval(new ThesisApproval());
        app.getThesisApproval().setStatus(ApprovalStatus.APPROVED);

        when(applicationRepo.findByStudent_UserInfo_EmailAndActiveTrue("student@mail.com"))
                .thenReturn(Optional.of(app));
        when(statementRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        ThesisStatementDTO input = new ThesisStatementDTO();
        input.setTitle("My Title");
        input.setBody("My Body");
        ThesisStatementDTO result = service.create(input);

        assertEquals("My Title", result.getTitle());
    }

    @Test
    void create_shouldFail_whenNotApproved() {
        setSecurityContext("student@mail.com");

        ThesisApplication app = new ThesisApplication();
        ThesisApproval approval = new ThesisApproval();
        approval.setStatus(ApprovalStatus.REJECTED);
        app.setThesisApproval(approval);

        when(applicationRepo.findByStudent_UserInfo_EmailAndActiveTrue("student@mail.com"))
                .thenReturn(Optional.of(app));
        ThesisStatementDTO input = new ThesisStatementDTO();
        input.setTitle("My Title");
        input.setBody("My Body");

        assertThrows(ForbiddenActionException.class, () ->
                service.create(input));
    }

    @Test
    void create_shouldFail_whenAlreadyExists() {
        setSecurityContext("student@mail.com");

        ThesisApproval approval = new ThesisApproval();
        approval.setStatus(ApprovalStatus.APPROVED);


        ThesisApplication app = new ThesisApplication();
        app.setThesisApproval(approval);
        app.setThesisStatement(new ThesisStatement());

        ThesisStatementDTO input = new ThesisStatementDTO();
        input.setTitle("My Title");
        input.setBody("My Body");

        when(applicationRepo.findByStudent_UserInfo_EmailAndActiveTrue("student@mail.com"))
                .thenReturn(Optional.of(app));

        assertThrows(ConflictException.class, () ->
                service.create(input));
    }

    @Test
    void gradeThesis_shouldSucceed_andGraduate() {
        setSecurityContext("teacher@mail.com");

        var teacher = new Teacher();
        teacher.setId(1L);
        var student = new Student();
        student.setId("s1");
        var application = new ThesisApplication();
        application.setActive(true);
        var statement = new ThesisStatement();
        statement.setThesisApplication(application);

        student.setThesisApplications(List.of(application));

        when(teacherRepo.findByUserInfo_Email("teacher@mail.com")).thenReturn(Optional.of(teacher));
        when(studentRepo.findById("s1")).thenReturn(Optional.of(student));
        when(defenceRepo.existsByStudents_Id("s1")).thenReturn(true);
        when(defenceRepo.existsByStudents_IdAndTeachers_Id("s1", 1L)).thenReturn(true);
        when(statementRepo.findByThesisApplicationId(any())).thenReturn(Optional.of(statement));

        GradeThesisDTO dto = new GradeThesisDTO("s1", 5);
        assertDoesNotThrow(() -> service.gradeThesis(dto));
        verify(statementRepo).save(any());
    }

    @Test
    void gradeThesis_shouldThrow_ifGradeTooLow() {
        setSecurityContext("teacher@mail.com");

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Student student = new Student();
        student.setId("s1");

        ThesisApplication app = new ThesisApplication();
        app.setActive(true);
        student.setThesisApplications(List.of(app));
        ThesisStatement statement = new ThesisStatement();

        when(teacherRepo.findByUserInfo_Email(any())).thenReturn(Optional.of(teacher));
        when(studentRepo.findById("s1")).thenReturn(Optional.of(student));
        when(defenceRepo.existsByStudents_Id(any())).thenReturn(true);
        when(defenceRepo.existsByStudents_IdAndTeachers_Id(any(), any())).thenReturn(true);
        when(statementRepo.findByThesisApplicationId(any())).thenReturn(Optional.of(statement));

        assertThrows(ConflictException.class, () ->
                service.gradeThesis(new GradeThesisDTO("s1", 1)));
    }

    @Test
    void gradeThesis_shouldThrow_ifAlreadyGraded() {
        setSecurityContext("teacher@mail.com");

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Student student = new Student();
        student.setId("s1");

        ThesisApplication app = new ThesisApplication();
        app.setActive(true);
        student.setThesisApplications(List.of(app));
        ThesisStatement statement = new ThesisStatement();
        statement.setGrade(6);

        when(teacherRepo.findByUserInfo_Email(any())).thenReturn(Optional.of(teacher));
        when(studentRepo.findById(any())).thenReturn(Optional.of(student));
        when(defenceRepo.existsByStudents_Id(any())).thenReturn(true);
        when(defenceRepo.existsByStudents_IdAndTeachers_Id(any(), any())).thenReturn(true);
        when(statementRepo.findByThesisApplicationId(any())).thenReturn(Optional.of(statement));

        assertThrows(ConflictException.class, () ->
                service.gradeThesis(new GradeThesisDTO("s1", 5)));
    }

    @Test
    void gradeThesis_shouldThrow_ifTeacherNotInDefence() {
        setSecurityContext("teacher@mail.com");

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Student student = new Student();
        student.setId("s1");

        ThesisApplication app = new ThesisApplication();
        app.setActive(true);
        student.setThesisApplications(List.of(app));

        when(teacherRepo.findByUserInfo_Email(any())).thenReturn(Optional.of(teacher));
        when(studentRepo.findById(any())).thenReturn(Optional.of(student));
        when(defenceRepo.existsByStudents_Id("s1")).thenReturn(true);
        when(defenceRepo.existsByStudents_IdAndTeachers_Id("s1", 1L)).thenReturn(false);

        assertThrows(ConflictException.class, () ->
                service.gradeThesis(new GradeThesisDTO("s1", 5)));
    }

    @Test
    void findByGradeRange_shouldReturnStatements() {
        ThesisStatement s1 = new ThesisStatement();
        s1.setTitle("A");
        s1.setBody("B");
        ThesisStatement s2 = new ThesisStatement();
        s2.setTitle("X");
        s2.setBody("Y");

        when(statementRepo.findAllByGradeBetween(3, 5)).thenReturn(List.of(s1, s2));
        var results = service.findByGradeRange(3, 5);

        assertEquals(2, results.size());
    }


}
