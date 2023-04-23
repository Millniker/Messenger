package com.friends.entity.DTO.BannedFriendDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BannedFriendPageDto {
    public int page;
    public int size;
    public List<BannedFriendDtoForPage> friendDto;
}
