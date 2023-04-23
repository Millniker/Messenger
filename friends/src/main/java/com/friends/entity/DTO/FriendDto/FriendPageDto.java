package com.friends.entity.DTO.FriendDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FriendPageDto {
    public int page;
    public int size;
    public List<FriendDtoForPage> friendDto;
}
