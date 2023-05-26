package com.chat.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatPageDto {
    public int page;
    public int size;
    public List<ChatDto> chatDtos;
}
