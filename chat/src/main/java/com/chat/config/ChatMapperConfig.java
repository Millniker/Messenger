package com.chat.config;

import com.chat.entity.Chat;
import com.chat.entity.DTO.*;
import com.chat.entity.Message;
import com.chat.mapper.ChatMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class ChatMapperConfig {
    @Bean
    public ChatMapper friendsMapper(){return new ChatMapper() {
        @Override
        public MessageDto toDto(Message message) {
            return new MessageDto(
                    message.getId(),
                    message.getSendDate(),
                    message.getMessageText(),
                    null,
                    null,
                    message.getAttachments().stream().map(AttachmentDto::toAttacmentDto).collect(Collectors.toList())
            );
        }
        @Override
        public Message toEntity(CreateDialogMessageDto createMessageDto) {
            return new Message(
                    UUID.randomUUID(),
                    null,
                    null,
                    createMessageDto.getText(),
                    null,
                    new ArrayList<>()
                    );
        }

        @Override
        public Message toEntity(CreateChatMessageDto createMessageDto) {
            return new Message(
                    UUID.randomUUID(),
                    null,
                    null,
                    createMessageDto.getText(),
                    null,
                    new ArrayList<>()
            );
        }

        @Override
        public ChatInfoDto toInfoDto(Chat chat) {
            return new ChatInfoDto(
                    chat.getName(),
                    chat.getAvatar(),
                    chat.getAdmin(),
                    chat.getDate()
            );
        }

        @Override
        public DialogInfoDto toDialogInfoDto(Chat chat) {
            return new DialogInfoDto(
                    chat.getName(),
                    chat.getDate()
            );
        }

        @Override
        public Chat toChatEntity(CreateChatDto createUpdateChatDto) {
            return new Chat(
                    UUID.randomUUID(),
                    null,
                    createUpdateChatDto.getName(),
                    null,
                    null,
                    null,
                    new ArrayList<>()
            );
        }


    };
    }

}
