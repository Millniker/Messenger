package com.user.controllers;

import com.user.entity.DTO.UserDto;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integration")
public class IntegrationController {
    private final UserService userService;
    @GetMapping("/{login}")
    public UserDto getUserByLogin(@PathVariable String login){
        return userService.getUserByLoginForIntegration(login);
    }
}
