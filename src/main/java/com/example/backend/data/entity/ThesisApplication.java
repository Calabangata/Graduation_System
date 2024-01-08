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

    @Column(name = "topic")
    private String topic;
    @Column(name = "purpose")
    private String purpose;
    @Column(name = "tasks")
    private String tasks;
    @Column(name = "teck_stack")
    private String techStack;
    @Column(name = "is_approved")
    private boolean isApproved;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @OneToOne(mappedBy = "thesisApplication")
    private ThesisStatement thesisStatement;


}
