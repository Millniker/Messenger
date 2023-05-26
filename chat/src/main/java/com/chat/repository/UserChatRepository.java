package com.chat.repository;

import com.chat.entity.Chat;
import com.chat.entity.Message;
import com.chat.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserChatRepository extends JpaRepository<UserChat, UUID> {
    @Query(value = "SELECT c FROM Chat c " +
            "JOIN UserChat uc1 ON uc1.chatId = c.id AND uc1.userId = :user_id1 " +
            "JOIN UserChat uc2 ON uc2.chatId = c.id AND uc2.userId = :user_id2 " +
            "WHERE c.type = 'Dialog'")
    Chat findAllByUserChatOptions (UUID user_id1, UUID user_id2);
    @Query(value = "SELECT uc.userId FROM UserChat uc WHERE uc.chatId = :chat_id")
    List<UUID> findByChatIdOptions (UUID chat_id);
    @Query(value = "SELECT uc FROM UserChat uc WHERE uc.chatId = :chat AND uc.userId <> :user ")
    UserChat findUsersByDialogOptions(UUID user, UUID chat);
    @Query(value = "SELECT m FROM UserChat uc JOIN Message m ON uc.chatId = m.chatId WHERE uc.userId = :user_id AND m.chatId = uc.chatId AND LOWER(m.messageText) LIKE LOWER(CONCAT('%', :searchText, '%')) ")
    List<Message> findMessageOption (UUID user_id, String searchText);
    @Query(value = "SELECT m FROM UserChat uc JOIN Attachment a ON uc.chatId = a.chatId JOIN Message m ON m.id = a.messageId WHERE uc.userId = :user_id AND a.chatId = uc.chatId AND LOWER(a.fileName) LIKE LOWER(CONCAT('%', :searchText, '%')) ")
    List<Message> findAttachmentOption (UUID user_id, String searchText);
    @Query(value = "SELECT c FROM UserChat uc JOIN Chat c ON uc.chatId = c.id WHERE uc.userId = :user")
    Set<Chat> findChatsOption(UUID user);
    @Query(value = "SELECT c FROM UserChat uc JOIN Chat c ON uc.chatId = c.id WHERE uc.userId = :user_id AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Set<Chat> findChatsByUserAndSearch(UUID user_id, String name);
}