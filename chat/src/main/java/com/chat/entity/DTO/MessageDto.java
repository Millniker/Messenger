package com.chat.entity.DTO;

import com.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDto {
    public UUID id;
    public LocalDate sendDate;
    public String messageText;
    public String senderName;
    public UUID avatarId;
    public List<AttachmentDto> attachments;
    public static MessageDto messageToDto(Message message){
        return new MessageDto(
                message.getId(),
                message.getSendDate(),
                message.getMessageText(),
                null,
                null,
                message.getAttachments().stream().map(AttachmentDto::toAttacmentDto).collect(Collectors.toList())

        );
    }
}
