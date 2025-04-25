package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThesisApplicationRepository extends JpaRepository<ThesisApplication, Long> {
    boolean existsByStudentIdAndActiveTrue(String studentId);

    Optional<ThesisApplication> findByIdAndStudent_UserInfo_Email(Long id, String email);

    Optional<ThesisApplication> findByStudent_UserInfo_EmailAndActiveTrue(String email);

}
