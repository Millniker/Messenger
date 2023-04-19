package com.user.service;

import com.user.entity.DTO.*;
import com.user.entity.User;

public interface UserService {
    User register(SignUpDto signUpDto);
    User login (SignInDto signInDto);
    UserDto getMe();
    UserDto updateUser(UserUpdateDto userUpdateDto);
    UserPageDto getUsers (SortsAndFiltersDto sortsAndFiltersDto);
    UserDto getUserByLogin(String login);


    }
