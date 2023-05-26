package com.chat.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatDto {
    public UUID id;
    public String name;
    public String text;
    public Boolean isHaveAttachment;
    public LocalDate sendDate;
    public UUID senderId;
}
