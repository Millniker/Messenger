package com.friends.service;

import com.friends.entity.DTO.BannedFriendDto.AddBannedFriendsDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendPageDto;
import com.friends.entity.DTO.CheckBanDto;
import com.friends.entity.DTO.SearchFilterDto;
import com.friends.entity.DTO.SortsAndFiltersDto;

import java.util.UUID;

public interface BannedFriendService {
    BannedFriendPageDto findBanFriends(SearchFilterDto searchFilterDto);
    BannedFriendPageDto getBanFriends (SortsAndFiltersDto sortsAndFiltersDto);
    BannedFriendDto getUserById(UUID id);
    void deleteBanFriend(UUID id);
    void addBanFriend(AddBannedFriendsDto addBannedFriendsDto);
    void patchBanUser (String login);
    Boolean isBanned(CheckBanDto checkBanDto);
}
