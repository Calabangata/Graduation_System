package com.example.backend.security.controller;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.dto.LoginUserDTO;
import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.dto.response.CurrentUserResponse;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.security.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDTO loginUserDTO, HttpServletResponse response) {
        LoginResponse loginResponse = authenticationService.login(loginUserDTO);
        
        // Set refresh token as httpOnly, secure cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // Set to true in production with HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        response.addCookie(refreshTokenCookie);
        
        // Return only accessToken (not refreshToken in body)
        LoginResponse responseBody = new LoginResponse();
        responseBody.setAccessToken(loginResponse.getAccessToken());
        responseBody.setExpirationTime(loginResponse.getExpirationTime());
        
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }
        
        LoginResponse loginResponse = authenticationService.refresh(refreshToken);
        
        // Update refresh token cookie if a new one was issued
        if (loginResponse.getRefreshToken() != null && !loginResponse.getRefreshToken().isEmpty()) {
            Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // Set to true in production with HTTPS
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            refreshTokenCookie.setAttribute("SameSite", "Strict");
            response.addCookie(refreshTokenCookie);
        }
        
        // Return only accessToken
        LoginResponse responseBody = new LoginResponse();
        responseBody.setAccessToken(loginResponse.getAccessToken());
        responseBody.setExpirationTime(loginResponse.getExpirationTime());
        
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        
        if (refreshToken != null) {
            authenticationService.logout(refreshToken);
        }
        
        // Clear refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // Expire immediately
        response.addCookie(refreshTokenCookie);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CurrentUserResponse> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        
        CurrentUserResponse response = new CurrentUserResponse();
        response.setFirstName(userInfo.getFirstName());
        response.setLastName(userInfo.getLastName());
        response.setEmail(userInfo.getEmail());
        response.setRole(userInfo.getRole().getName().toString());
        
        return ResponseEntity.ok(response);
    }
    
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
