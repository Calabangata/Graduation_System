package com.example.backend.service;

import com.example.backend.data.entity.Department;
import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.Teacher;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.DepartmentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.dto.DepartmentDTO;
import com.example.backend.dto.UserInfoDTO;
import com.example.backend.enums.UserRole;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void createDepartment_shouldPersistSuccessfully() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setDepartmentName("IT");
        Department saved = new Department();
        saved.setId(1L);
        saved.setName("IT");

        when(departmentRepository.save(any())).thenReturn(saved);

        Department result = departmentService.createDepartment(dto);

        assertNotNull(result);
        assertEquals("IT", result.getName());
        verify(departmentRepository).save(any());
    }

    @Test
    void assignTeacherToDepartment_shouldSucceed() {
        Department department = new Department();
        department.setName("Engineering");

        Teacher teacher = new Teacher();
        teacher.setId(10L);

        when(departmentRepository.findByName("Engineering")).thenReturn(Optional.of(department));
        when(teacherRepository.findById(10L)).thenReturn(Optional.of(teacher));

        departmentService.assignTeacherToDepartment("Engineering", 10L);

        assertEquals(department, teacher.getDepartment());
        verify(teacherRepository).save(teacher);
    }

    @Test
    void assignTeacherToDepartment_shouldThrowIfAlreadyAssigned() {
        Department oldDept = new Department();
        oldDept.setName("Math");

        Department newDept = new Department();
        newDept.setName("Physics");

        Teacher teacher = new Teacher();
        teacher.setId(10L);
        teacher.setDepartment(oldDept);

        when(departmentRepository.findByName("Physics")).thenReturn(Optional.of(newDept));
        when(teacherRepository.findById(10L)).thenReturn(Optional.of(teacher));

        assertThrows(ConflictException.class,
                () -> departmentService.assignTeacherToDepartment("Physics", 10L));
    }

    @Test
    void getTeachersByDepartmentName_shouldReturnMappedList() {
        Department dept = new Department();
        dept.setName("Software");
        Role role = new Role();
        role.setName(UserRole.TEACHER);


        UserInfo user = new UserInfo();
        user.setId(1L);
        user.setFirstName("Ada");
        user.setLastName("Lovelace");
        user.setEmail("ada@cs.com");
        user.setRole(role);

        Teacher teacher = new Teacher();
        teacher.setUserInfo(user);
        teacher.setDepartment(dept);

        when(departmentRepository.findByName("Software")).thenReturn(Optional.of(dept));
        when(teacherRepository.findAllByDepartment(dept)).thenReturn(List.of(teacher));

        List<UserInfoDTO> result = departmentService.getTeachersByDepartmentName("Software");

        assertEquals(1, result.size());
        assertEquals("Ada", result.getFirst().getFirstName());
    }

    @Test
    void getTeachersByDepartmentName_shouldThrowIfNotFound() {
        when(departmentRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getTeachersByDepartmentName("Nonexistent"));
    }

    @Test
    void createDepartment_shouldThrowIfAlreadyExists() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setDepartmentName("IT");

        when(departmentRepository.existsByName("IT")).thenReturn(true);

        ConflictException ex =assertThrows(ConflictException.class,
                () -> departmentService.createDepartment(dto));
        assertEquals("Department with name 'IT' already exists", ex.getMessage());
    }
}
