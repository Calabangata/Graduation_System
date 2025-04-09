package com.example.backend.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull
    @Column(name = "topic", nullable = false)
    private String topic;

    @NotNull
    @Column(name = "purpose", nullable = false)
    private String purpose;

    @NotNull
    @Column(name = "tasks", nullable = false)
    private String tasks;

    @NotNull
    @Column(name = "teck_stack", nullable = false)
    private String techStack;

    @Column(name = "is_approved")
    private boolean isApproved;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "thesis_approval_id", referencedColumnName = "id", unique = true)
    private ThesisApproval thesisApproval;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToOne(mappedBy = "thesisApplication")
    private ThesisStatement thesisStatement;




}
