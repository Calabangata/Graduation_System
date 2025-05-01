package com.example.backend.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "department")
    private List<Teacher> teachers;

    @OneToMany(mappedBy = "department")
    private List<ThesisApproval> thesisApprovals;

    @OneToMany(mappedBy = "department")
    private List<ThesisDefence> thesisDefences;

}
