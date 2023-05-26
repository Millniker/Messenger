package com.user.controllers;

import com.user.entity.DTO.UserDto;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integration")
@Slf4j
public class IntegrationController {
    private final UserService userService;
    @GetMapping("/{login}")
    public UserDto getUserByLogin(@PathVariable String login){
        return userService.getUserByLoginForIntegration(login);
    }
    @GetMapping("/name/{id}")
    public String getUserNameById(@PathVariable String id){
        return userService.getUserByIdForIntegration(id);
    }
    @GetMapping("/login/{id}")
    public UUID getUserLoginById(@PathVariable String id){
        return userService.getAvatarByIdForIntegration(id);
    }
}
