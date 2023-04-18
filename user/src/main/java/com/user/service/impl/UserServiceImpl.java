package com.user.service.impl;

import com.common.model.JwtUser;
import com.user.entity.DTO.SignInDto;
import com.user.entity.DTO.SignUpDto;
import com.user.entity.DTO.UserDto;
import com.user.entity.User;
import com.user.exceptions.UserLoginIsAlreadyExist;
import com.user.exceptions.WrongCredentialsException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
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
    @Override
    public User login (SignInDto signInDto){
        User user = userRepository.findByLogin(signInDto.getLogin());
        if(user ==null && !Objects.equals(user.getPassword(), passwordEncoder.encode(signInDto.getPassword()))){
            throw new WrongCredentialsException();
        }
        return user;
    }
    @Override
    public UserDto getMe( JwtUser user){
        User user1 = userRepository.findById(user.getId()).orElse(null);
        return userMapper.toDto(user1);
    }

    public List<UserDto> getUsers(int offset, int limit){
        return userRepository.findAll(PageRequest.of(offset, limit)).stream().map(userMapper::toDto).collect(Collectors.toList());
    }

}
