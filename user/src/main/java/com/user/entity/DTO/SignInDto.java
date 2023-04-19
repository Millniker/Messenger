package com.user.entity.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor
@Data
public class SignInDto {
    @NotBlank(message = "пароль не должен быть пустым")
    private String password;
    @NotBlank
    private String login;
}
