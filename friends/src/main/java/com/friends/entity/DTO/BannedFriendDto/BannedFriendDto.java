package com.friends.entity.DTO.BannedFriendDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
public class BannedFriendDto {
    private UUID id;
    private LocalDateTime addTime;
    private LocalDateTime deleteTime;
    private String mainId;
    private String addedFriendId;
    private String firstName;
    private String secondName;
    private String patronymic;
}
