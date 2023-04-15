package com.user.service.impl;

import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;
import com.user.exceptions.UserLoginIsAlreadyExist;
import com.user.exceptions.UserRegistrationException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Signature;
import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Clock clock;
    private PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public User register (SignUpDto signUpDto){
        if(userRepository.existsByLogin(signUpDto.getLogin())){
            throw  new UserLoginIsAlreadyExist();
        }
        String password = passwordEncoder.encode(signUpDto.getPassword());
        User user = userMapper.toEntity(signUpDto);
        user.setPassword(password);
        user.setRegistrationDate(LocalDate.now(clock));
        return userRepository.save(user);
    }

}
