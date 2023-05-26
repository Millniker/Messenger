package com.chat.mapper;

import com.chat.entity.Chat;
import com.chat.entity.DTO.*;
import com.chat.entity.Message;

public interface ChatMapper {
        MessageDto toDto(Message message);
        Message toEntity(CreateDialogMessageDto createMessageDto);
        Message toEntity(CreateChatMessageDto createMessageDto);

        ChatInfoDto toInfoDto(Chat chat);
        DialogInfoDto toDialogInfoDto (Chat chat);
        Chat toChatEntity(CreateChatDto createUpdateChatDto);
}

