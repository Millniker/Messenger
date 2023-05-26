package com.user.service;

import com.user.entity.DTO.*;
import com.user.entity.User;

import java.util.UUID;

public interface UserService {
    User register(SignUpDto signUpDto);
    User login (SignInDto signInDto);
    UserDto getMe();
    UserDto updateUser(UserUpdateDto userUpdateDto);
    UserPageDto getUsers (SortsAndFiltersDto sortsAndFiltersDto);
    UserDto getUserByLogin(String login);
    UserDto getUserByLoginForIntegration(String login);
    String getUserByIdForIntegration(String id);
    UUID getAvatarByIdForIntegration(String id);



}
