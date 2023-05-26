package com.notify.entity.DTO;

import com.notify.entity.enums.NotifyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotifyDTO {
    public UUID userId;
    public NotifyType type;
    public String text;
}
