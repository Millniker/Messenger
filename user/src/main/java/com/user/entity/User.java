package com.user.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    private String patronymic;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    private String number;
    private UUID avatar;
    private String city;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    @Column(unique = true, nullable = false)
    private String login;
}
