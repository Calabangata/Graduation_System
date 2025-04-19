package com.example.backend.security.controller;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.dto.LoginUserDTO;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.security.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserInfo> register(@RequestBody RegisterUserDTO registerUserDTO) {
        UserInfo userInfo = authenticationService.registerUser(registerUserDTO);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDTO loginUserDTO) {
        return ResponseEntity.ok(authenticationService.login(loginUserDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestParam String token) {
        return ResponseEntity.ok(authenticationService.refresh(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String email) {
        authenticationService.logout(email);
        return ResponseEntity.noContent().build();
    }
}
