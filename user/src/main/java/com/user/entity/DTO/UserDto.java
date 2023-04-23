package com.user.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID Id;
    private String firstName;
    private String secondName;
    private String patronymic;
    private LocalDate birthDate;
    private String email;
    private String number;
    private UUID avatar;
    private String city;
    private LocalDateTime registrationDate;
    private String login;
}
