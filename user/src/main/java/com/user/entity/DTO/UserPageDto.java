package com.user.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserPageDto {
    public int page;
    public int size;
    public List<UserDto> userDto;
}
