package com.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "chat_id")
    private UUID chatId;
    @Column(name = "send_date")
    private LocalDate sendDate;
    @Column(name = "message_text")
    private String messageText;
    @Column(name = "sender_id")
    private UUID senderId;
    @OneToMany
    private List<Attachment> attachments;

}
