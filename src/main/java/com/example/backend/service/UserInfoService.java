package com.example.backend.service;

import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.RoleRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.enums.UserRole;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

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


    public void activateUser(String email) {
        Optional<UserInfo> optionalUser = userInfoRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            UserInfo user = optionalUser.get();
            if (user.isActive()) {
                throw new ConflictException("User is already active");
            }
            user.setActive(true);
            userInfoRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }

    public void deactivateUser(String email) {
        Optional<UserInfo> optionalUser = userInfoRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            UserInfo user = optionalUser.get();
            if (!user.isActive()) {
                throw new ConflictException("User is already inactive");
            }
            authenticationService.logout(user.getEmail());
            user.setActive(false);
            userInfoRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }
}
