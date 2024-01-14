package com.example.backend.data.entity;

import com.example.backend.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "thesis_approval_id")
    private ThesisApproval thesisApproval;

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
}
