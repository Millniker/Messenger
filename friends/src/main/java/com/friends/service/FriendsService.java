package com.friends.service;

import com.friends.entity.DTO.CheckFriendDto;
import com.friends.entity.DTO.FriendDto.AddFriendDto;
import com.friends.entity.DTO.FriendDto.FriendDto;
import com.friends.entity.DTO.FriendDto.FriendPageDto;
import com.friends.entity.DTO.SearchFilterDto;
import com.friends.entity.DTO.SortsAndFiltersDto;

import java.util.UUID;

public interface FriendsService {
    FriendPageDto findFriends(SearchFilterDto searchFilterDto);
    FriendPageDto getFriends (SortsAndFiltersDto sortsAndFiltersDto);
    FriendDto getUserById(UUID id);
    void deleteFriend(UUID id);
    void addFriends(AddFriendDto addFriendDto);
    Boolean isFriend(CheckFriendDto checkFriendDto);

}
