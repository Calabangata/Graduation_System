package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThesisApplicationRepository extends JpaRepository<ThesisApplication, Long> {
}
