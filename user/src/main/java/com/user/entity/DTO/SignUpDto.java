package com.user.entity.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Data
public class SignUpDto {
    private String firstName;
    private String secondName;
    private String patronymic;
    private LocalDate birthDate;
    @NotBlank(message = "пароль не должен быть пустым")
    @Min(6)
    private String password;
    @NotBlank
    @Email
    private String email;
    private String number;
    private UUID avatar;
    private String city;
    @NotBlank
    private String login;
}
