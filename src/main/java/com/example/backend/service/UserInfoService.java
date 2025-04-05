package com.example.backend.service;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.data.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public List<UserInfo> getAllUsers() {
        return userInfoRepository.findAll();
    }
}
