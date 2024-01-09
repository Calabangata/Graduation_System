package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThesisStatementRepository extends JpaRepository<ThesisStatement, Long> {
}
