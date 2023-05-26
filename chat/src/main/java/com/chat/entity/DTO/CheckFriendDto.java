package com.chat.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CheckFriendDto {
    public UUID mainId;
    public UUID userId;
}
