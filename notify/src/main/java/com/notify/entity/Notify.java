package com.notify.entity;

import com.notify.entity.enums.NotifyType;
import com.notify.entity.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notif")
public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private NotifyType type;
    private String text;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "read_status")
    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus;
    @Column(name = "readed_time")
    private LocalDateTime readedTime;
    @Column(name = "send_date")
    private LocalDateTime sendDate;
}
