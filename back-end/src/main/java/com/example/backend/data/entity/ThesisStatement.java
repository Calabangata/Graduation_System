package com.example.backend.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ThesisStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "body", nullable = false)
    private String body;

    @CreationTimestamp
    @Column(name = "date_of_upload", nullable = false)
    private LocalDateTime dateOfUpload;

    @Min(2)
    @Max(6)
    @Column(name = "grade")
    private Integer grade;

    @OneToOne
    @JoinColumn(name = "thesis_application_id", unique = true)
    private ThesisApplication thesisApplication;

    @OneToOne(mappedBy = "thesisStatement")
    private ThesisReview thesisReview;
}
