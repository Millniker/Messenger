package com.friends.controllers;

import com.friends.entity.DTO.FriendDto.AddFriendDto;
import com.friends.entity.DTO.FriendDto.FriendDto;
import com.friends.entity.DTO.FriendDto.FriendPageDto;
import com.friends.entity.DTO.SearchFilterDto;
import com.friends.entity.DTO.SortsAndFiltersDto;
import com.friends.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendsController {
    private final FriendsService friendsService;
    @PostMapping
    public ResponseEntity<FriendPageDto> getFriendsList(@RequestBody SortsAndFiltersDto sortsAndFiltersDto){
        return ResponseEntity.ok(friendsService.getFriends(sortsAndFiltersDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriend(@PathVariable UUID id){
        return ResponseEntity.ok(friendsService.getUserById(id));
    }
    @PostMapping("/add")
    public ResponseEntity<Void> addFriend(@RequestBody AddFriendDto addFriendDto){
        friendsService.addFriends(addFriendDto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFriend (@PathVariable UUID id){
        friendsService.deleteFriend(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/search")
    public ResponseEntity<FriendPageDto> searchFriend(@RequestBody SearchFilterDto searchFilterDto){
        return ResponseEntity.ok(friendsService.findFriends(searchFilterDto));
    }

}
