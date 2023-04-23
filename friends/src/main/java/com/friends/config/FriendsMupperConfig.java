package com.friends.config;

import com.friends.entity.BannedFriend;
import com.friends.entity.DTO.BannedFriendDto.AddBannedFriendsDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendDto;
import com.friends.entity.DTO.FriendDto.AddFriendDto;
import com.friends.entity.DTO.FriendDto.FriendDto;
import com.friends.entity.Friend;
import com.friends.mapper.FriendsMupper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
@Configuration
public class FriendsMupperConfig{
    @Bean
    public FriendsMupper friendsMupper(){return new FriendsMupper() {
        @Override
        public FriendDto toDto(Friend friend) {
            return new FriendDto(
                    friend.getId(),
                    friend.getAddTime(),
                    friend.getDeleteTime(),
                    friend.getMainId(),
                    friend.getAddedFriendId(),
                    friend.getFirstName(),
                    friend.getSecondName(),
                    friend.getPatronymic()
            );
        }
        @Override
        public Friend toEntity(AddFriendDto friendDto) {
            return new Friend(
                    UUID.randomUUID(),
                    null,
                    null,
                    null,
                    friendDto.getAddedFriendId(),
                    friendDto.getFirstName(),
                    friendDto.getSecondName(),
                    friendDto.getPatronymic(),
                    "false"
            );
        }

        @Override
        public BannedFriendDto toBanDto(BannedFriend bannedFriend) {
            return new BannedFriendDto(
                    bannedFriend.getId(),
                    bannedFriend.getAddTime(),
                    bannedFriend.getDeleteTime(),
                    bannedFriend.getMainId(),
                    bannedFriend.getAddedFriendId(),
                    bannedFriend.getFirstName(),
                    bannedFriend.getSecondName(),
                    bannedFriend.getPatronymic()
            );
        }

        @Override
        public BannedFriend toBanEntity(AddBannedFriendsDto bannedFriendsDto) {
            return new BannedFriend(
                    UUID.randomUUID(),
                    null,
                    null,
                    null,
                    bannedFriendsDto.getAddedFriendId(),
                    bannedFriendsDto.getFirstName(),
                    bannedFriendsDto.getSecondName(),
                    bannedFriendsDto.getPatronymic(),
                    "false"
            );
        }
    };
    }

}
