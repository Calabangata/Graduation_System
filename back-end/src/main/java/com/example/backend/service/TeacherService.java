package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class TeacherService {

    private final UserInfoService userInfoService;

    public void deactivateTeacherAccount(String email) {
        userInfoService.deactivateUser(email);
    }
}
