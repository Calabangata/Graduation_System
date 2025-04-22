package com.example.backend.config.seed;

import com.example.backend.dto.request.RegisterUserDTO;
import com.example.backend.enums.AcademicRank;
import com.example.backend.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Order(3)
public class UsersSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final AuthenticationService authenticationService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.createTeachers();
        this.createStudents();
    }

    private void createTeachers() {
        IntStream.rangeClosed(1,4)
                .mapToObj(i -> new RegisterUserDTO("Teacher", "T" + i, "teacher" + i + "@example.com", "teacher123", "TEACHER", AcademicRank.PROFESSOR.name()))
                .forEach(authenticationService::createSeededUser);

    }

    private void createStudents() {
        IntStream.rangeClosed(1, 4)
                .mapToObj(i -> new RegisterUserDTO("Student", "S" + i, "student" + i + "@example.com", "student123", "STUDENT", null))
                .forEach(authenticationService::createSeededUser);
    }
}
