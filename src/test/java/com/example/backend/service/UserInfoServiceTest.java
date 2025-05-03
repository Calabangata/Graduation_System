package com.example.backend.service;

import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.RoleRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.enums.UserRole;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.security.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserInfoServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationService authenticationService;

    @InjectMocks
    private UserInfoService service;
//    @InjectMocks
//    private AuthenticationService authenticationService;

    @Test
    void getAllUsers_shouldReturnAll() {
        when(userInfoRepository.findAll()).thenReturn(List.of(new UserInfo(), new UserInfo()));
        assertEquals(2, service.getAllUsers().size());
    }

    @Test
    void createAdministrator_shouldCreateAdmin() {
        RegisterUserDTO dto = new RegisterUserDTO();
        Role role = new Role();
        role.setName(UserRole.ADMIN);

        dto.setRole(role.getName().name());
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("admin@example.com");
        dto.setPassword("pass");


        when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userInfoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserInfo created = service.createAdministrator(dto);
        assertEquals("admin@example.com", created.getEmail());
        assertEquals("encoded", created.getPassword());
    }

    @Test
    void createAdministrator_shouldReturnNull_ifNoRoleFound() {
        when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.empty());
        RegisterUserDTO dto = new RegisterUserDTO();
        assertNull(service.createAdministrator(dto));
    }

    @Test
    void activateUser_shouldActivateInactiveUser() {
        UserInfo user = new UserInfo(); user.setEmail("test@example.com"); user.setActive(false);
        when(userInfoRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        service.activateUser("test@example.com");
        assertTrue(user.isActive());
        verify(userInfoRepository).save(user);
    }

    @Test
    void activateUser_shouldThrow_ifAlreadyActive() {
        UserInfo user = new UserInfo(); user.setActive(true);
        when(userInfoRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        assertThrows(ResourceNotFoundException.class, () -> service.activateUser("test@example.com"));
    }

    @Test
    void activateUser_shouldThrow_ifNotFound() {
        when(userInfoRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.activateUser("test@example.com"));
    }

    @Test
    void deactivateUser_shouldDeactivate_ifActive() {
        UserInfo user = new UserInfo(); user.setEmail("a@b.com"); user.setActive(true);
        when(userInfoRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        service.deactivateUser("a@b.com");
        assertFalse(user.isActive());
        verify(authenticationService).logout("a@b.com");
        verify(userInfoRepository).save(user);
    }

    @Test
    void deactivateUser_shouldThrow_ifAlreadyInactive() {
        UserInfo user = new UserInfo(); user.setActive(false);
        when(userInfoRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        assertThrows(ResourceNotFoundException.class, () -> service.deactivateUser("a@b.com"));
    }

    @Test
    void deactivateUser_shouldThrow_ifNotFound() {
        when(userInfoRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deactivateUser("a@b.com"));
    }

}
