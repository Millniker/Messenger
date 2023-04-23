package com.friends.controllers;

import com.friends.entity.DTO.CheckBanDto;
import com.friends.service.BannedFriendService;
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
    @PostMapping("/check")
    public boolean checkBan(@RequestBody CheckBanDto checkBanDto){
        return bannedFriendService.isBanned(checkBanDto);
    }

}
