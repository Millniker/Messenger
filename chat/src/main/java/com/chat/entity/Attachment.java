package com.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "message_id")
    private UUID messageId;
    @Column(name = "attachment_id")
    private UUID attachmentId;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "chat_id")
    private UUID chatId;
}
