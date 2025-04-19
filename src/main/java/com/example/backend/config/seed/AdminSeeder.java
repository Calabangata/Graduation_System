package com.example.backend.config.seed;

import com.example.backend.data.entity.Role;
import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.RoleRepository;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.enums.UserRole;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;
    private final UserInfoRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(RoleRepository roleRepository, UserInfoRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdmin();
    }

    private void createSuperAdmin() {
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setFirstName("General");
        registerUserDTO.setLastName("Krulev");
        registerUserDTO.setEmail("admin@email.com");
        registerUserDTO.setPassword("123456");

        Optional<Role> optionalRole = roleRepository.findByName(UserRole.SUPER_ADMIN);
        Optional<UserInfo> optionalUser = userRepository.findByEmail(registerUserDTO.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }
        UserInfo user = new UserInfo();

        user.setFirstName(registerUserDTO.getFirstName());
        user.setLastName(registerUserDTO.getLastName());
        user.setEmail(registerUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }
}
