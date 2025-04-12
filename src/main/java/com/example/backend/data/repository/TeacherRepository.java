package com.example.backend.data.repository;

import com.example.backend.data.entity.Department;
import com.example.backend.data.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findAllByDepartment(Department department);

}
