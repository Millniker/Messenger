package com.user.service.impl;

import com.common.model.JwtUser;
import com.user.entity.DTO.*;
import com.user.entity.User;
import com.user.exceptions.CommonException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final UserMapper userMapper;
    @Transactional
    @Override
    public User register (SignUpDto signUpDto){
        if(userRepository.existsByLogin(signUpDto.getLogin())){
            throw CommonException.builder().message("Пользователь с таким login уже существует").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            throw CommonException.builder().message("Пользователь с таким email уже существует").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        String password = passwordEncoder.encode(signUpDto.getPassword());
        User user = userMapper.toEntity(signUpDto);
        user.setPassword(password);
        user.setRegistrationDate(LocalDateTime.now(clock));
        return userRepository.save(user);
    }
    @Override
    public User login (SignInDto signInDto){
        User user = userRepository.findByLogin(signInDto.getLogin());

        if(user==null){
            throw CommonException.builder().message("Пользователя с таким login не существует").httpStatus(HttpStatus.UNAUTHORIZED).build();
        }
        if(!passwordEncoder.matches(signInDto.getPassword(),user.getPassword())){
            throw CommonException.builder().message("Неправильные данные для входа").httpStatus(HttpStatus.UNAUTHORIZED).build();
        }
        return user;
    }
    @Override
    public UserDto getMe(){
        JwtUser jwtUserData =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(jwtUserData.getId()).orElse(null);
        return userMapper.toDto(user);
    }
    @Override
    public UserPageDto getUsers (SortsAndFiltersDto sortsAndFiltersDto) {
        Specification<User> filterSpec = createFilter(sortsAndFiltersDto.getFilters());
        Sort sort = createSort(sortsAndFiltersDto.getSorts());
        PageRequest pageRequest = PageRequest.of(sortsAndFiltersDto.getPage(),sortsAndFiltersDto.getSize(),sort);
        Page<User> userPage = userRepository.findAll(filterSpec,pageRequest);
        List<UserDto> users= userPage.getContent().stream().map(this::toDto).toList();
        return new UserPageDto(
                sortsAndFiltersDto.getPage(),
                users.size(),
                users
        );
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto){
        JwtUser jwtUserData =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(jwtUserData.getId()).orElse(null);
        user.setFirstName(userUpdateDto.getFirstName()!=null? userUpdateDto.getFirstName():user.getFirstName());
        user.setSecondName(userUpdateDto.getSecondName()!=null? userUpdateDto.getSecondName():user.getSecondName());
        user.setPatronymic(userUpdateDto.getPatronymic()!=null? userUpdateDto.getPatronymic():user.getPatronymic());
        user.setBirthDate(userUpdateDto.getBirthDate()!=null? userUpdateDto.getBirthDate():user.getBirthDate());
        user.setNumber(userUpdateDto.getNumber()!=null? userUpdateDto.getNumber():user.getNumber());
        user.setAvatar(userUpdateDto.getAvatar()!=null? userUpdateDto.getAvatar():user.getAvatar());
        userRepository.save(user);
        return userMapper.toDto(user);
    }
    @Override
    public UserDto getUserByLogin(String login){
        User user = userRepository.findByLogin(login);
        if(user==null){
            throw CommonException.builder().message("Пользователь с таким login не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return userMapper.toDto(user);
    }

    private Specification<User> createFilter(Map<String,String> filters){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for(Map.Entry<String,String> filter :filters.entrySet()){
                String filterField = filter.getKey();
                String filterValue = filter.getValue();
                if(Arrays.stream(User.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(filterField))){
                    Predicate predicate = criteriaBuilder.equal(root.get(filterField),filterValue);
                    predicates.add(predicate);
                }
                else {
                    throw CommonException.builder().message("Неправильно указано поле").httpStatus(HttpStatus.BAD_REQUEST).build();
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    private Sort createSort (Map<String,String> sorts){
        List<Sort.Order> orders = new ArrayList<>();
        for(Map.Entry<String,String> sortParam :sorts.entrySet()){
            String sortField = sortParam.getKey();
            Sort.Direction sortDir = Sort.Direction.fromString(sortParam.getValue());
            if(Arrays.stream(User.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(sortField))){
                orders.add(new Sort.Order(sortDir,sortField));
            }
            else {
                throw CommonException.builder().message("Неправильно указано поле").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
            return Sort.by(orders);
    }
    private UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getSecondName(),
                user.getPatronymic(),
                user.getBirthDate(),
                user.getPassword(),
                user.getEmail(),
                user.getNumber(),
                user.getAvatar(),
                user.getCity(),
                user.getRegistrationDate(),
                user.getLogin());
    }

}
