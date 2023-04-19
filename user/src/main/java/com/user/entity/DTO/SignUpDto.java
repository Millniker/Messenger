package com.user.entity.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
    @Pattern(regexp = "/\\(?([0-9]{3})\\)?([ .-]?)([0-9]{3})\\2([0-9]{4})/")
    private String number;
    private UUID avatar;
    private String city;
    @NotBlank
    private String login;
}
