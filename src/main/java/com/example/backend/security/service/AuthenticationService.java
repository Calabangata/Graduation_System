package com.example.backend.security.service;

import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.Student;
import com.example.backend.data.entity.Teacher;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.RoleRepository;
import com.example.backend.data.repository.StudentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.dto.LoginUserDTO;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.enums.UserRole;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.security.data.entity.RefreshToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(UserInfoRepository userInfoRepository, RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, StudentRepository studentRepository, TeacherRepository teacherRepository, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserInfo registerUser(RegisterUserDTO registerUserDTO) {

        if (userInfoRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + registerUserDTO.getEmail() + " already exists");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(registerUserDTO.getFirstName());
        userInfo.setLastName(registerUserDTO.getLastName());
        userInfo.setEmail(registerUserDTO.getEmail());
        userInfo.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));

        Optional<Role> role = roleRepository.findByName(UserRole.valueOf(registerUserDTO.getRole()));
        if (role.isEmpty()) {
            throw new IllegalArgumentException("Invalid role specified");
        }
        userInfo.setRole(role.get());
        UserInfo savedUser = userInfoRepository.save(userInfo);
        if (UserRole.STUDENT.name().equals(registerUserDTO.getRole())) {
            Student student = new Student();
            student.setUserInfo(savedUser);
            studentRepository.save(student);
        } else if (UserRole.TEACHER.name().equals(registerUserDTO.getRole())) {
            Teacher teacher = new Teacher();
            teacher.setUserInfo(savedUser);
            teacherRepository.save(teacher);
        }
        return savedUser;
    }

    public UserInfo authenticateUser(LoginUserDTO loginUserDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getEmail(), loginUserDTO.getPassword())
        );
        return userInfoRepository.findByEmail(loginUserDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public LoginResponse login(LoginUserDTO loginUserDTO) {
        UserInfo user = authenticateUser(loginUserDTO);

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken.getToken(), jwtService.getExpirationTime());

    }

    public LoginResponse refresh(String token) {
        RefreshToken refreshToken = refreshTokenService.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        UserInfo user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);

        return new LoginResponse(newAccessToken, token, jwtService.getExpirationTime());
    }

    public void logout(String email) {
        UserInfo user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.deleteByUser(user);
    }
}
