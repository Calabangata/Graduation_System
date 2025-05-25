package com.example.backend.controller;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RestController
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfo> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        List<UserInfo> users = userInfoService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @PatchMapping("/{email}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable String email) {
        userInfoService.activateUser(email);
        return ResponseEntity.noContent().build();
    }
}
