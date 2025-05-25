package com.example.backend.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Student {

    @Id
    @Column(nullable = false, updatable = false)
    private String id; // facultyNumber

    @OneToOne
    @JoinColumn(name = "user_info_id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "student")
    private List<ThesisApplication> thesisApplications;

    @Column(name = "is_graduated")
    private boolean graduated = false;
}
