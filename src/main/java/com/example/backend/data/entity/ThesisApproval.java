package com.example.backend.data.entity;

import com.example.backend.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
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

    @OneToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "thesisApproval")
    private List<TeacherApproval> teacherApprovals;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
}
