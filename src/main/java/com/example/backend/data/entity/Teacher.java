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
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "academic_rank")
    private String academicRank;

    @OneToMany(mappedBy = "teacher")
    private List<Student> students;

    @OneToOne
    @JoinColumn(name = "user_info_id")
    private UserInfo userInfo;
}
