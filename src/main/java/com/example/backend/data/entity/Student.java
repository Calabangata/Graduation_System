package com.example.backend.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_info_id")
    private userInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
