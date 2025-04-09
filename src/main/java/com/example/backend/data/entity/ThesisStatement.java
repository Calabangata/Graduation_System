package com.example.backend.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
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
    @NotNull
    @Column(name = "date_of_upload", nullable = false)
    private LocalDate dateOfUpload;


    @Min(value = 2, message = "The grade must be at least 2!")
    @Max(value = 6, message = "The grade must not be higher than 6!")
    @Column(name = "grade", nullable = false)
    private int grade;
    @OneToOne
    @JoinColumn(name = "thesis_application_id")
    private ThesisApplication thesisApplication;
}
