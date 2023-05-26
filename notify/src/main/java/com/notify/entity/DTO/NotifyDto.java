package com.notify.entity.DTO;

import com.notify.entity.Notify;
import com.notify.entity.enums.NotifyType;
import com.notify.entity.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyDto {
    public UUID id;
    public NotifyType type;
    public String text;
    public ReadStatus readStatus;
    public LocalDateTime sendDate;
    public static NotifyDto toDto(Notify notify) {
        return new NotifyDto(
                notify.getId(),
                notify.getType(),
                notify.getText(),
                notify.getReadStatus(),
                notify.getSendDate()
        );
    }
}

