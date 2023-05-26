package com.friends.controllers;

import com.friends.entity.DTO.CheckBanDto;
import com.friends.entity.DTO.CheckFriendDto;
import com.friends.service.BannedFriendService;
import com.friends.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integration")
@RequiredArgsConstructor
public class IntegrationController {
    private final BannedFriendService bannedFriendService;
    private final FriendsService friendsService;
    @PostMapping("/check")
    public boolean checkBan(@RequestBody CheckBanDto checkBanDto){
        return bannedFriendService.isBanned(checkBanDto);
    }
    @PostMapping("/friend")
    public boolean getFriend(@RequestBody CheckFriendDto checkFriendDto){
        return friendsService.isFriend(checkFriendDto);
    }
}
