package com.example.backend.data.repository;

import com.example.backend.data.entity.ThesisApplication;
import com.example.backend.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThesisApplicationRepository extends JpaRepository<ThesisApplication, Long> {
    boolean existsByStudentIdAndActiveTrue(String studentId);

    Optional<ThesisApplication> findByIdAndStudent_UserInfo_Email(Long id, String email);

    Optional<ThesisApplication> findByStudent_UserInfo_EmailAndActiveTrue(String email);

    List<ThesisApplication> findAllByThesisApproval_Status(ApprovalStatus status);

    List<ThesisApplication> findAllByThesisApproval_StatusAndSupervisor_Id(ApprovalStatus status, Long supervisorId);

    @Query("""
                SELECT a FROM ThesisApplication a
                WHERE LOWER(a.topic) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<ThesisApplication> findAllByTopicContaining(@Param("keyword") String keyword);

}
