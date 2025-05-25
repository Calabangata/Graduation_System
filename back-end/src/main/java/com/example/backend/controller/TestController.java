package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/test")
public class TestController {

    @GetMapping("/hello")
    public String Hello() {

        return "Hello World";
    }
}
