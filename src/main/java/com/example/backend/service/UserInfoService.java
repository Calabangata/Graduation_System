package com.example.backend.service;

import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.Student;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.RoleRepository;
import com.example.backend.data.repository.StudentRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.enums.UserRole;
import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;

    public UserInfoService(UserInfoRepository userInfoRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, StudentRepository studentRepository) {
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
    }

    public List<UserInfo> getAllUsers() {
        return userInfoRepository.findAll();
    }

    public UserInfo createAdministrator(RegisterUserDTO registerUserDTO) {
        Optional<Role> optionalRole = roleRepository.findByName(UserRole.ADMIN);

        if (optionalRole.isEmpty()) {
            return null;
        }
        UserInfo user = new UserInfo();
        user.setFirstName(registerUserDTO.getFirstName());
        user.setLastName(registerUserDTO.getLastName());
        user.setEmail(registerUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        return userInfoRepository.save(user);
    }

    public void deleteStudentByFacultyNumber(String facultyNumber) {
        Optional<Student> optionalStudent = studentRepository.findById(facultyNumber);
        if (optionalStudent.isEmpty()) {
            throw new ResourceNotFoundException("Student with faculty number " + facultyNumber + " not found");
        }

        Student student = optionalStudent.get();
        UserInfo user = student.getUserInfo();

        studentRepository.delete(student);
        userInfoRepository.delete(user);
    }

    public void deleteTeacherByEmail(String email) {
        Optional<UserInfo> optionalUser = userInfoRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Teacher with email " + email + " not found");
        }

        if(optionalUser.get().getRole().getName() != UserRole.TEACHER) {
            throw new ResourceNotFoundException("User with email " + email + " is not a teacher");
        }
        UserInfo user = optionalUser.get();
        userInfoRepository.delete(user);
    }
}
