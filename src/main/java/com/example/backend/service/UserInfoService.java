package com.example.backend.service;

import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.RoleRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.RegisterUserDTO;
import com.example.backend.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInfoService(UserInfoRepository userInfoRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
}
