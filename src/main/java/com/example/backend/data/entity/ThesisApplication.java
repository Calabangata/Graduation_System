package com.example.backend.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThesisApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "topic", nullable = false)
    private String topic;
    @NonNull
    @Column(name = "purpose", nullable = false)
    private String purpose;
    @NonNull
    @Column(name = "tasks", nullable = false)
    private String tasks;
    @NonNull
    @Column(name = "teck_stack", nullable = false)
    private String techStack;
    @Column(name = "is_approved")
    private boolean isApproved;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @OneToOne(mappedBy = "thesisApplication")
    private ThesisStatement thesisStatement;

    @OneToOne(mappedBy = "thesisApplication")
    private ThesisApproval thesisApproval;


}
