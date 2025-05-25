package com.example.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private UserInfoService userInfoService;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void deactivateTeacherAccount_shouldDelegateToUserInfoService() {
        String email = "teacher@example.com";
        teacherService.deactivateTeacherAccount(email);
        verify(userInfoService).deactivateUser(email);
    }
}
