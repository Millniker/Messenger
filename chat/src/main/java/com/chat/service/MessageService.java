package com.chat.service;

import com.chat.entity.DTO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void sendMessageInDialog(CreateDialogMessageDto createMessageDto, List<MultipartFile> files);
    void sendMessageInChat(UUID id, CreateChatMessageDto messageDto, List<MultipartFile> files);
    ChatInfoDto getChatInfo(UUID id);
    void createChat(CreateChatDto createUpdateChatDto, MultipartFile file);
    void updateChat(UUID chatId, UpdateChatDto updateChatDto, MultipartFile file);
    List<MessageDto> getMessages (UUID id);
    List<ChatDto> getChats (PageDto pageDto);
    List<FindedMessagesDto> findMessages(FindMessageDto findMessageDto);


    }
