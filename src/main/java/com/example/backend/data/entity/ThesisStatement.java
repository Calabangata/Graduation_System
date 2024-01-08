package com.example.backend.data.entity;

import jakarta.persistence.*;
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

    @Column(name = "title")
    private String title;
    @Column(name = "body")
    private String body;
    @Column(name = "date_of_upload")
    private LocalDate dateOfUpload;
    @OneToOne
    @JoinColumn(name = "thesis_application_id")
    private ThesisApplication thesisApplication;
}
