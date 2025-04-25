package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserInfoService userInfoService;

    public void deactivateStudentAccount(String email) {
        userInfoService.deactivateUser(email);
    }
}
