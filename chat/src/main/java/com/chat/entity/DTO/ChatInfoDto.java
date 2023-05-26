package com.chat.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatInfoDto {
    public String name;
    public UUID avatar;
    public UUID admin;
    public LocalDate createDate;
}
