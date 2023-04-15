package com.user.service;

import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;
import com.user.exceptions.UserRegistrationException;
import com.user.repository.UserRepository;
import com.user.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User register(SignUpDto signUpDto);

}
