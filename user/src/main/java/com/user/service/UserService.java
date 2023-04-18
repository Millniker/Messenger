package com.user.service;

import com.common.model.JwtUser;
import com.user.entity.DTO.SignInDto;
import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;

import java.util.List;

public interface UserService {
    User register(SignUpDto signUpDto);
    User login (SignInDto signInDto);
    UserDto getMe(JwtUser user);
    List<UserDto> getUsers(int offset, int limit);


    }
