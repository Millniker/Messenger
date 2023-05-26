package com.chat.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FindedMessagesDto {
    public UUID chatId;
    public String chatName;

    public String text;
    public Boolean isHaveAttachment;
    public LocalDate sendDate;
    public List<String> attachmentName;
}
