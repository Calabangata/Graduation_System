package com.example.backend.security.service;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.UserInfoRepository;
import com.example.backend.dto.LoginUserDTO;
import com.example.backend.dto.RegisterUserDTO;
import com.example.backend.exception.UserAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserInfoRepository userInfoRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
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

        return userInfoRepository.save(userInfo);
    }

    public UserInfo authenticateUser(LoginUserDTO loginUserDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getEmail(), loginUserDTO.getPassword())
        );

        return userInfoRepository.findByEmail(loginUserDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
