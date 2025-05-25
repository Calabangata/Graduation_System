package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThesisApprovalRepository extends JpaRepository<ThesisApproval, Long> {
}
