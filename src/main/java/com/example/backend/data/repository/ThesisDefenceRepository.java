package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisDefence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThesisDefenceRepository extends JpaRepository<ThesisDefence, Long> {
    // Custom query methods can be defined here if needed
    boolean existsByStudents_Id(String studentId);

    boolean existsByStudents_IdAndTeachers_Id(String id, Long id1);

    Optional<ThesisDefence> findByStudents_Id(String studentId);
}
