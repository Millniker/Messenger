package com.friends.entity.DTO.BannedFriendDto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
@Data
@RequiredArgsConstructor
public class AddBannedFriendsDto {
    @NotBlank
    private String addedFriendId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String secondName;
    @NotBlank
    private String patronymic;
}
