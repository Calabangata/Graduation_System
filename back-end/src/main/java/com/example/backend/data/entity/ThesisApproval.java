package com.example.backend.data.entity;

import com.example.backend.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThesisApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "thesisApproval")
    private ThesisApplication thesisApplication;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "thesisApproval")
    private List<TeacherApproval> teacherApprovals;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
}
