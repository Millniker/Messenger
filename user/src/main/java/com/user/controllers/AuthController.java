package com.user.controllers;

import com.user.entity.DTO.UserDto;
import com.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {
    private UserService userService;
    @PostMapping("/register")
    public String registerUser() {
//        String jwtToken = userService.registerUser(registrationRequest);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + jwtToken);
        return "Work!";
//        return new ResponseEntity<>(registrationRequest, headers, HttpStatus.CREATED);
    }
}