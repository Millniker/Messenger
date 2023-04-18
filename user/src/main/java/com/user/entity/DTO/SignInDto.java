package com.user.entity.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class SignInDto {
    private String password;
    private String login;
}
