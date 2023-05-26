package com.notify.entity.DTO;

import com.notify.entity.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStatusDto {
    public List<UUID> notify;
    public ReadStatus readStatus;
}
