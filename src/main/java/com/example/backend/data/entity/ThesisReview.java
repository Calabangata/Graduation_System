package com.example.backend.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThesisReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "body", nullable = false)
    private String body;

    @NotNull
    @Column(name = "approval_decision", nullable = false)
    private String approvalDecision;

    @CreationTimestamp
    @Column(name = "date_of_upload", nullable = false)
    private LocalDateTime dateOfUpload;

    @OneToOne
    @JoinColumn(name = "thesis_statement_id", unique = true)
    private ThesisStatement thesisStatement;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Teacher reviewer;
}
