package com.example.backend.util;

import com.example.backend.data.repository.StudentRepository;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
public class FacultyNumberGenerator {
    private static final String PREFIX = "F";
    private static final int DIGITS = 6;
    private static final int MAX_ATTEMPTS = 10;
    private final Random random = new Random();
    private final StudentRepository studentRepository;

    public FacultyNumberGenerator(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public String generateUnique() {
        return IntStream.range(0, MAX_ATTEMPTS)
                .mapToObj(i -> PREFIX + String.format("%0" + DIGITS + "d", random.nextInt((int) Math.pow(10, DIGITS))))
                .filter(fn -> studentRepository.findByFacultyNumber(fn).isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to generate unique faculty number after " + MAX_ATTEMPTS + " attempts"));
    }
}
