package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThesisReviewRepository extends JpaRepository<ThesisReview, Long> {
    boolean existsByThesisStatementId(Long thesisStatementId);

    ThesisReview findByThesisStatementId(Long thesisStatementId);
}
