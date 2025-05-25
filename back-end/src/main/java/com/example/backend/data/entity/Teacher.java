package com.example.backend.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "academic_rank", nullable = false)
    private String academicRank;

    @OneToMany(mappedBy = "teacher")
    private List<Student> students;

    @OneToOne
    @JoinColumn(name = "user_info_id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "supervisor")
    private List<ThesisApplication> submittedApplications;

    @OneToMany(mappedBy = "teacher")
    private List<TeacherApproval> teacherApprovals;

    @OneToMany(mappedBy = "reviewer")
    private List<ThesisReview> thesisReviews;

}
