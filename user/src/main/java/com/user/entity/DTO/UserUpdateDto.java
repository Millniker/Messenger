package com.user.entity.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserUpdateDto {
    private String firstName;
    private String secondName;
    private String patronymic;
    private LocalDate birthDate;
    private String number;
    private UUID avatar;
    private String city;
}
