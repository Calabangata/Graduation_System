package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThesisStatementRepository extends JpaRepository<ThesisStatement, Long> {
    @Query("""
                SELECT ts FROM ThesisStatement ts
                WHERE ts.grade BETWEEN :minGrade AND :maxGrade
            """)
    List<ThesisStatement> findAllByGradeBetween(@Param("minGrade") int minGrade, @Param("maxGrade") int maxGrade);
}
