package com.user.mapper;

import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(SignUpDto signUpDto);
}
