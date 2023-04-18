package com.user.entity.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Data
public class SignUpDto {
    private String firstName;
    private String secondName;
    private String patronymic;
    private LocalDate birthDate;
    private String password;
    private String email;
    private String number;
    private UUID avatar;
    private String city;
    private String login;
}
