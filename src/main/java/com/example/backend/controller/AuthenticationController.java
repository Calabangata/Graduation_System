package com.example.backend.controller;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.LoginUserDTO;
import com.example.backend.dto.RegisterUserDTO;
import com.example.backend.security.service.JwtService;
import com.example.backend.security.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserInfo> register(@RequestBody RegisterUserDTO registerUserDTO) {
        UserInfo userInfo = authenticationService.registerUser(registerUserDTO);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDTO loginUserDTO) {
        UserInfo authenticatedUser = authenticationService.authenticateUser(loginUserDTO);
        String token = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setExpirationTime(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
