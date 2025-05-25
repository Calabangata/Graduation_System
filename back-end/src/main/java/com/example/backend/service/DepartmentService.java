package com.example.backend.service;

import com.example.backend.data.entity.Department;
import com.example.backend.data.entity.Teacher;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.DepartmentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.dto.DepartmentDTO;
import com.example.backend.dto.UserInfoDTO;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentService(TeacherRepository teacherRepository, DepartmentRepository departmentRepository) {
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(DepartmentDTO departmentDTO) {
        if(departmentRepository.existsByName(departmentDTO.getDepartmentName())) {
            throw new ConflictException("Department with name '" + departmentDTO.getDepartmentName() + "' already exists");
        }
        Department department = new Department();
        department.setName(departmentDTO.getDepartmentName());
        return departmentRepository.save(department);
    }

    public void assignTeacherToDepartment(String departmentName, Long teacherId) {
        Department department = departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new ResourceNotFoundException("Department with name '" + departmentName + "' not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getDepartment() != null) {
            throw new ConflictException("Teacher is already assigned to a department");
        }
        teacher.setDepartment(department);
        teacherRepository.save(teacher);
    }

    public List<UserInfoDTO> getTeachersByDepartmentName(String departmentName) {
        Department department = departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new ResourceNotFoundException("Department with name '" + departmentName + "' not found"));

        return teacherRepository.findAllByDepartment(department)
                .stream()
                .map(teacher -> {
                    UserInfo user = teacher.getUserInfo();
                    UserInfoDTO dto = new UserInfoDTO();
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole().getName().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
