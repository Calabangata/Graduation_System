package com.example.backend.controller;

import com.example.backend.data.entity.Department;
import com.example.backend.dto.DepartmentDTO;
import com.example.backend.dto.UserInfoDTO;
import com.example.backend.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/departments")
@RestController
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/new")
    public ResponseEntity<Department> createDepartment(@RequestBody DepartmentDTO department) {
        Department saved = departmentService.createDepartment(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{departmentName}/add-teacher/{teacherId}")
    public ResponseEntity<Void> assignTeacherToDepartment(@PathVariable String departmentName, @PathVariable Long teacherId) {
        departmentService.assignTeacherToDepartment(departmentName, teacherId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{departmentName}/teachers")
    public ResponseEntity<List<UserInfoDTO>> getTeachersByDepartmentName(@PathVariable String departmentName) {
        List<UserInfoDTO> teachers = departmentService.getTeachersByDepartmentName(departmentName);
        return ResponseEntity.ok(teachers);
    }

}
