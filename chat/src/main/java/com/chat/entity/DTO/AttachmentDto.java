package com.chat.entity.DTO;

import com.chat.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentDto {
    public UUID id;
    public UUID messageId;
    public UUID attachmentId;
    public String fileName;
    public static Attachment toAttacmentEntity(AttachmentDto attachmentDto) {
        return new Attachment(
                UUID.randomUUID(),
                null,
                null,
                attachmentDto.getFileName(),
                null
        );
    }
    public static AttachmentDto toAttacmentDto(Attachment attachment) {
        return new AttachmentDto(
                UUID.randomUUID(),
                attachment.getMessageId(),
                attachment.getAttachmentId(),
                attachment.getFileName()
        );
    }
}
