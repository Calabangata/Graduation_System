package com.example.backend.controller;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.dto.RegisterUserDTO;
import com.example.backend.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/admin")
@RestController
public class AdminController {

    private final UserInfoService userInfoService;

    public AdminController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserInfo> createAdministrator(@RequestBody RegisterUserDTO registerUserDto) {
        UserInfo createdAdmin = userInfoService.createAdministrator(registerUserDto);
        return ResponseEntity.ok(createdAdmin);
    }
}
