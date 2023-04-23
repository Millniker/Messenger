package com.user.config;

import com.common.model.JwtUser;
import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;
import com.user.mapper.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class MapperConfig {
    @Bean
    public UserMapper userMapper(){return new UserMapper() {
        @Override
        public UserDto toDto(User user) {
            return new UserDto(user.getId(),
                    user.getFirstName(),
                    user.getSecondName(),
                    user.getPatronymic(),
                    user.getBirthDate(),
                    user.getEmail(),
                    user.getNumber(),
                    user.getAvatar(),
                    user.getCity(),
                    user.getRegistrationDate(),
                    user.getLogin());
        }

        @Override
        public User toEntity(SignUpDto signUpDto) {
            return new User(
                    UUID.randomUUID(),
                    signUpDto.getFirstName(),
                    signUpDto.getSecondName(),
                    signUpDto.getPatronymic(),
                    signUpDto.getBirthDate(),
                    signUpDto.getPassword(),
                    signUpDto.getEmail(),
                    signUpDto.getNumber(),
                    signUpDto.getAvatar(),
                    signUpDto.getCity(),
                    null,
                    signUpDto.getLogin()
            );
        }

        @Override
        public JwtUser toJwtUser(User user) {
            return new JwtUser(user.getId(),
                    user.getLogin(),
                    user.getFirstName());
        }
    };}

}
