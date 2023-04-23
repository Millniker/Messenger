package com.friends.entity.DTO.BannedFriendDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
public class BannedFriendDtoForPage {
    private UUID id;
    private LocalDateTime addTime;
    private LocalDateTime deleteTime;
    private String addedFriendId;
    private String firstName;
    private String secondName;
    private String patronymic;
}
