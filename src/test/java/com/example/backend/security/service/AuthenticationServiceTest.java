package com.example.backend.security.service;

import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.RoleRepository;
import com.example.backend.data.repository.StudentRepository;
import com.example.backend.data.repository.TeacherRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.LoginUserDTO;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.enums.AcademicRank;
import com.example.backend.enums.UserRole;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.security.data.entity.RefreshToken;
import com.example.backend.util.FacultyNumberGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private FacultyNumberGenerator facultyNumberGenerator;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void registerUser_shouldCreateStudentSuccessfully() {
        RegisterUserDTO dto = new RegisterUserDTO("John", "Doe", "john@example.com", "pass", "STUDENT", null);
        Role role = new Role();
        role.setName(UserRole.STUDENT);

        when(userInfoRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(UserRole.STUDENT)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userInfoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(facultyNumberGenerator.generateUnique()).thenReturn("FN123");

        UserInfo result = service.registerUser(dto);

        assertEquals(dto.getEmail(), result.getEmail());
        verify(studentRepository).save(any());
    }

    @Test
    void registerUser_shouldCreateTeacherSuccessfully() {
        RegisterUserDTO dto = new RegisterUserDTO("John", "Doe", "john@example.com", "pass", "TEACHER", AcademicRank.SENIOR_ASSISTANT.name());
        Role role = new Role();
        role.setName(UserRole.TEACHER);

        when(userInfoRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(UserRole.TEACHER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userInfoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserInfo result = service.registerUser(dto);

        assertEquals(dto.getEmail(), result.getEmail());
        verify(teacherRepository).save(any());
    }

    @Test
    void registerUser_shouldThrow_WhenEmailExists() {
        RegisterUserDTO dto = new RegisterUserDTO("John", "Doe", "john@example.com", "pass", "STUDENT", null);
        when(userInfoRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new UserInfo()));
        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> service.registerUser(dto));
        assertEquals("User with email " + dto.getEmail() + " already exists", ex.getMessage());
    }

    @Test
    void login_shouldGenerateAccessAndRefreshTokens() {
        LoginUserDTO dto = new LoginUserDTO("user@example.com", "pass");
        UserInfo user = new UserInfo();
        user.setEmail("user@example.com");
        RefreshToken token = new RefreshToken();
        token.setToken("refresh");

        when(userInfoRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("access");
        when(jwtService.getExpirationTime()).thenReturn(3600L);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(token);

        LoginResponse response = service.login(dto);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        assertEquals(3600L, response.getExpirationTime());
    }

    @Test
    void refresh_shouldGenerateNewAccessTokenFromRefreshToken() {
        UserInfo user = new UserInfo();
        user.setEmail("john@example.com");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh");
        refreshToken.setUser(user);

        when(refreshTokenService.findByToken("refresh")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtService.generateToken(user)).thenReturn("new-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        LoginResponse response = service.refresh("refresh");

        assertEquals("new-token", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        assertEquals(3600L, response.getExpirationTime());
    }

    @Test
    void logout_shouldClearRefreshTokensForUser() {
        UserInfo user = new UserInfo();
        user.setEmail("logout@example.com");
        when(userInfoRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        service.logout(user.getEmail());
        verify(refreshTokenService).deleteByUser(user);
    }

}
