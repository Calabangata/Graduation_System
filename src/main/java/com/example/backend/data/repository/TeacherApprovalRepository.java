package com.example.backend.data.repository;

import com.example.backend.data.entity.TeacherApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherApprovalRepository extends JpaRepository<Long, TeacherApproval> {
}
