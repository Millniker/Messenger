package com.user.controllers;

import com.common.model.JwtUser;
import com.common.service.JwtService;
import com.user.entity.DTO.SignInDto;
import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;
import com.user.mapper.UserMapper;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody SignUpDto signUpDto) {
        User user = userService.register(signUpDto);
        String token = jwtService.generateToken(userMapper.toJwtUser(user));
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION,token).body(userMapper.toDto(user));
    }
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody SignInDto signInDto) {
        User user = userService.login(signInDto);
        String token = jwtService.generateToken(userMapper.toJwtUser(user));
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION,token).body(userMapper.toDto(user));
    }
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal JwtUser user){
        UserDto user1 =userService.getMe(user);

        return ResponseEntity.ok(user1);
    }
    @PostMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam int offset, @RequestParam int limit){
        return ResponseEntity.ok(userService.getUsers(offset,limit));
    }
}