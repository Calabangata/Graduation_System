package com.example.backend.data.repository;

import com.example.backend.data.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    @Query("""
                SELECT COUNT(DISTINCT s)
                FROM Student s
                JOIN s.thesisApplications a
                JOIN a.thesisStatement st
                JOIN st.thesisReview r
                WHERE r.approvalDecision = :decision
            """)
    long countStudentsByReviewDecision(@Param("decision") String decision);

    @Query("SELECT COUNT(DISTINCT s.id) FROM Student s " +
            "JOIN s.thesisApplications a " +
            "JOIN a.thesisStatement st " +
            "WHERE a.supervisor.id = :supervisorId AND st.grade >= 3")
    long countSuccessfulDefencesBySupervisor(@Param("supervisorId") Long supervisorId);

    @Query("SELECT DISTINCT s FROM ThesisDefence d " +
            "JOIN d.students s " +
            "WHERE s.graduated = true AND d.date BETWEEN :startDate AND :endDate")
    List<Student> findGraduatedStudentsByDefenceDateRange(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    List<Student> findAllByIdIn(List<String> facultyNumbers);

}
