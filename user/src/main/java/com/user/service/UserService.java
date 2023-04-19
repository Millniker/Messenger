package com.user.service;

import com.user.entity.DTO.SignInDto;
import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;

import java.util.List;

public interface UserService {
    User register(SignUpDto signUpDto);
    User login (SignInDto signInDto);
    UserDto getMe();
    List<UserDto> getUsers(int offset, int limit);


    }
