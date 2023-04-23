package com.friends.controllers;

import com.friends.entity.DTO.BannedFriendDto.AddBannedFriendsDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendPageDto;
import com.friends.entity.DTO.SearchFilterDto;
import com.friends.entity.DTO.SortsAndFiltersDto;
import com.friends.service.BannedFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/friends/ban")
@RequiredArgsConstructor
public class BannedFriendController {
    private final BannedFriendService bannedFriendService;
    @PostMapping
    public ResponseEntity<BannedFriendPageDto> getBanFriendsList(@RequestBody SortsAndFiltersDto sortsAndFiltersDto){
        return ResponseEntity.ok(bannedFriendService.getBanFriends(sortsAndFiltersDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<BannedFriendDto> getBanFriend(@PathVariable UUID id){
        return ResponseEntity.ok(bannedFriendService.getUserById(id));
    }
    @PostMapping("/add")
    public ResponseEntity<Void> addBanFriend(@RequestBody AddBannedFriendsDto addBannedFriendsDto){
        bannedFriendService.addBanFriend(addBannedFriendsDto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFriend (@PathVariable UUID id){
        bannedFriendService.deleteBanFriend(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/search")
    public ResponseEntity<BannedFriendPageDto> searchFriend(@RequestBody SearchFilterDto searchFilterDto){
        return ResponseEntity.ok(bannedFriendService.findBanFriends(searchFilterDto));
    }
    @PatchMapping("/patch/{login}")
    public ResponseEntity<Void> patchUser(@PathVariable String login){
        bannedFriendService.patchBanUser(login);
        return ResponseEntity.ok().build();
    }


}
