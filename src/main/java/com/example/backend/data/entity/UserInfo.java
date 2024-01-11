package com.example.backend.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NonNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToOne(mappedBy = "userInfo")
    private Student student;

    @OneToOne(mappedBy = "userInfo")
    private Teacher teacher;
}
