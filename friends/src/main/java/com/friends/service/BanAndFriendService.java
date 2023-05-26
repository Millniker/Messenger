package com.friends.service;

import com.common.model.UpdateFIODto;
import com.friends.entity.BannedFriend;
import com.friends.entity.Friend;
import com.friends.repository.BannedFriendRepository;
import com.friends.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class BanAndFriendService {
    private final FriendRepository friendRepository;
    private final BannedFriendRepository bannedFriendRepository;

    /**
     *
     * patch метод для обновления ФИО пользователя при его изменений, получает запрос через RabbitMq
     */
    @Transactional
    public void patchUser(UpdateFIODto updateFIODto){
        List<Friend> friends = friendRepository.findAllByAddedFriendId(updateFIODto.getId().toString());
        for (Friend friend: friends) {
            friend.setFirstName(updateFIODto.getFirstname());
            friend.setSecondName(updateFIODto.getSeconfname());
            friend.setPatronymic(updateFIODto.getPatronomicname());
            friendRepository.save(friend);
        }
        List<BannedFriend> bannedFriends = bannedFriendRepository.findAllByAddedFriendId(updateFIODto.getId().toString());
        for (BannedFriend friend: bannedFriends) {
            friend.setFirstName(updateFIODto.getFirstname());
            friend.setSecondName(updateFIODto.getSeconfname());
            friend.setPatronymic(updateFIODto.getPatronomicname());
            bannedFriendRepository.save(friend);
        }
    }
}
