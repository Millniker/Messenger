package com.friends.entity.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class CheckFriendDto {
    public UUID mainId;
    public UUID userId;
}
