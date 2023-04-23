package com.friends.mapper;

import com.friends.entity.BannedFriend;
import com.friends.entity.DTO.BannedFriendDto.AddBannedFriendsDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendDto;
import com.friends.entity.DTO.FriendDto.AddFriendDto;
import com.friends.entity.DTO.FriendDto.FriendDto;
import com.friends.entity.Friend;

public interface FriendsMupper {

        FriendDto toDto(Friend friend);
        Friend toEntity(AddFriendDto friendDto);
        BannedFriendDto toBanDto(BannedFriend bannedFriend);
        BannedFriend toBanEntity(AddBannedFriendsDto bannedFriendsDto);
}

