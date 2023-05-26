package com.user.service.impl;

import com.common.model.CreateNotifyDTO;
import com.common.model.NotifyType;
import com.common.model.UpdateFIODto;
import com.common.security.props.SecurityProps;
import com.common.service.impl.JwtServiceImpl;
import com.user.entity.DTO.*;
import com.user.entity.User;
import com.user.exceptions.CommonException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import com.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.Predicate;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final UserMapper userMapper;
    private final SecurityProps securityProps;
    private final StreamBridge streamBridge;
    private final JwtServiceImpl jwtService;

    /**
     Регистрирует пользователя и возвращает токен и DTo пользователя
     */
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

    /**
     * Осуществляет вход пользователя,
     * принимает на вход login и password,
     * возвращает токен и DTo пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public User login (SignInDto signInDto){
        User user = userRepository.findByLogin(signInDto.getLogin());
        if(user==null){
            throw CommonException.builder().message("Пользователя с таким login не существует").httpStatus(HttpStatus.UNAUTHORIZED).build();
        }
        if(!passwordEncoder.matches(signInDto.getPassword(),user.getPassword())){
            throw CommonException.builder().message("Неправильные данные для входа").httpStatus(HttpStatus.UNAUTHORIZED).build();
        }

        sendByStreamBridge(new CreateNotifyDTO(
                user.getId(),
                NotifyType.LoggedIn,
                "Выполнен вход в аккаунт"
        ));
        return user;
    }

    /**
     *
     * Возвращает профиль авторизованного пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getMe(){
        User user = userRepository.findById(jwtService.getCurrentUserId()).orElse(null);
        if(user == null){
            throw CommonException.builder().message("Пользователь не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return userMapper.toDto(user);
    }

    /**
     *
     * Принимает на вход информацию о пагинации и фильтрах, сортировке,
     * возвращает удовлетворяемых пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public UserPageDto getUsers (SortsAndFiltersDto sortsAndFiltersDto) {
        if(sortsAndFiltersDto.getSize()==0){
            throw CommonException.builder().message("Size должен быть больше 0").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
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

    /**
     *
     *Позволяет изменить профиль пользователя
     */
    @Override
    @Transactional
    public UserDto updateUser(UserUpdateDto userUpdateDto){
        User user = userRepository.findById(jwtService.getCurrentUserId()).orElse(null);
        if(user ==null){
            throw CommonException.builder().message("Пользователя не существует").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        user.setFirstName(userUpdateDto.getFirstName()!=null? userUpdateDto.getFirstName():user.getFirstName());
        user.setSecondName(userUpdateDto.getSecondName()!=null? userUpdateDto.getSecondName():user.getSecondName());
        user.setPatronymic(userUpdateDto.getPatronymic()!=null? userUpdateDto.getPatronymic():user.getPatronymic());
        user.setBirthDate(userUpdateDto.getBirthDate()!=null? userUpdateDto.getBirthDate():user.getBirthDate());
        user.setNumber(userUpdateDto.getNumber()!=null? userUpdateDto.getNumber():user.getNumber());
        user.setAvatar(userUpdateDto.getAvatar()!=null? userUpdateDto.getAvatar():user.getAvatar());
        sendUpdateByStreamBridge(new UpdateFIODto(user.getId(),user.getFirstName(),user.getSecondName(),user.getPatronymic()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     *
     * метод для получения user по логину
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByLogin(String login){
        User user = userRepository.findByLogin(login);
        if(user==null){
            throw CommonException.builder().message("Пользователь с таким login не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        if(checkBan(user.getId())){
            throw CommonException.builder().message("Пользователь добавил вас в черный список").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return userMapper.toDto(user);
    }

    /**
     *
     * метод для получения user другими сервисами по login
     */
    @Override
    public UserDto getUserByLoginForIntegration(String login){
        User user = userRepository.findByLogin(login);
        if(user==null){
            throw CommonException.builder().message("Пользователь с таким login не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return userMapper.toDto(user);
    }

    /**
     *
     * получение ФИО юзера по id для других сервисов
     */
    @Override
    public String getUserByIdForIntegration(String id) {
        User user = userRepository.findById(UUID.fromString(id)).orElse(null);
        if(user==null){
            throw CommonException.builder().message("Пользователь с таким login не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return user.getFirstName() + " "+user.getSecondName()+" "+user.getPatronymic();
    }

    /**
     *
     * получение аватара пользователя по id для других сервисов
     */
    @Override
    public UUID getAvatarByIdForIntegration(String id) {
        User user = userRepository.findById(UUID.fromString(id)).orElse(null);
        if(user==null){
            throw CommonException.builder().message("Пользователь с таким login не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return user.getAvatar();
    }

    /**
     *
     * Парсит Map с фильтрами для дальнейшего поиска по фильтрам
     */
    private Specification<User> createFilter(Map<String,String> filters){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for(Map.Entry<String,String> filter :filters.entrySet()){
                String filterField = filter.getKey();
                String filterValue = filter.getValue();
                if(Arrays.stream(User.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(filterField))){
                    Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(filterField)),
                            "%" + filterValue.toLowerCase() + "%");
                    predicates.add(predicate);
                }
                else {
                    throw CommonException.builder().message("Неправильно указано поле").httpStatus(HttpStatus.BAD_REQUEST).build();
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     *
     *Парсит Map с полями для сортировки для дальнейшей сортировки
     */
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

    /**
     *Отправляет интеграционный запрос на сервис Friend, чтобы узнать забанен ли пользователь
     */
    private Boolean checkBan (UUID id){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("API_KEY", securityProps.getIntegrations().getApiKey());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        CheckBanDto body = new CheckBanDto(jwtService.getCurrentUserId(),id);
        HttpEntity<CheckBanDto> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Boolean> response = template.postForEntity(securityProps.getIntegrations().getUrl()[0],entity,Boolean.class );
            return response.getBody();
        }
        catch (HttpClientErrorException e){
                throw CommonException.builder().message(e.getMessage()).httpStatus(e.getStatusCode()).build();
        }
    }

    /**
     *
     * Конвертирует User в UserDto
     */
    private UserDto toDto(User user) {
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
    private void sendByStreamBridge(CreateNotifyDTO notifyDTO) {
        streamBridge.send("NewNotify-out-0", notifyDTO);
    }
    private void sendUpdateByStreamBridge(UpdateFIODto userUpdateDto) {
        streamBridge.send("UpdateUser-out-0", userUpdateDto);
    }

}
