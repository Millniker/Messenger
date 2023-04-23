package com.friends.service.impl;

import com.common.model.JwtUser;
import com.common.security.props.SecurityProps;
import com.friends.entity.DTO.FriendDto.AddFriendDto;
import com.friends.entity.DTO.FriendDto.FriendDto;
import com.friends.entity.DTO.FriendDto.FriendDtoForPage;
import com.friends.entity.DTO.FriendDto.FriendPageDto;
import com.friends.entity.DTO.SearchFilterDto;
import com.friends.entity.DTO.SortsAndFiltersDto;
import com.friends.entity.Friend;
import com.friends.exceptions.CommonException;
import com.friends.mapper.FriendsMupper;
import com.friends.repository.FriendRepository;
import com.friends.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.criteria.Predicate;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final FriendsMupper friendsMupper;
    private final FriendRepository friendRepository;
    private final SecurityProps securityProps;
    /**
     *
     * Принимает поля Id друга, Фио и добавляет его в друзья
     */
    @Override
    @Transactional
    public void addFriends(AddFriendDto addFriendDto){
        JwtUser jwtUserData =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Friend oldFriend = friendRepository.findByMainIdAndAddedFriendId(jwtUserData.getId().toString(),addFriendDto.getAddedFriendId());
        if(oldFriend!=null){
            if (oldFriend.getDeleteTime()!=null){
                oldFriend.setDeleteTime(null);
            }
            else {
                throw CommonException.builder().message("Пользователь уже добавлен в друзья").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        Friend friend = friendsMupper.toEntity(addFriendDto);
        friend.setAddTime(LocalDateTime.now());
        friend.setAddedFriendId(addFriendDto.getAddedFriendId());
        friend.setMainId(jwtUserData.getId().toString());
        friend.setDeleted("false");
        friendRepository.save(friend);
    }

    /**
     *
     * Принимает id пользователя и удаляет его из друзей
     */
    @Override
    @Transactional
    public void deleteFriend(UUID id){
        Friend friend = friendRepository.findByAddedFriendId(id.toString());
        if(friend==null){
            throw CommonException.builder().message("Пользователь не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        friend.setDeleteTime(LocalDateTime.now());
        friend.setDeleted("true");
        friendRepository.save(friend);
    }
    @Override
    public FriendDto getUserById(UUID id){
        Friend friend = friendRepository.findByAddedFriendId(id.toString());
        if(friend==null){
            throw CommonException.builder().message("Пользователь не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return friendsMupper.toDto(friend);
    }
    @Override
    public FriendPageDto getFriends (SortsAndFiltersDto sortsAndFiltersDto) {
        Map<String,String> filters = sortsAndFiltersDto.getFilters();
        filters.put("deleted","false");
        JwtUser jwtUserData =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        filters.put("mainId",jwtUserData.getId().toString() );
        sortsAndFiltersDto.setFilters(filters);
        Specification<Friend> filterSpec = createFilter(sortsAndFiltersDto.getFilters());
        Sort sort = createSort(sortsAndFiltersDto.getSorts());
        PageRequest pageRequest = PageRequest.of(sortsAndFiltersDto.getPage(),sortsAndFiltersDto.getSize(),sort);
        Page<Friend> friendPage = friendRepository.findAll(filterSpec,pageRequest);
        List<FriendDtoForPage> friendDtos= friendPage.getContent().stream().map(this::toDto).toList();
        return new FriendPageDto(
                sortsAndFiltersDto.getPage(),
                friendDtos.size(),
                friendDtos
        );
    }
    private Specification<Friend> createFilter(Map<String,String> filters){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for(Map.Entry<String,String> filter :filters.entrySet()){
                String filterField = filter.getKey();
                String filterValue = filter.getValue();
                if(Arrays.stream(Friend.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(filterField))){
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
    private Sort createSort (Map<String,String> sorts){
        List<Sort.Order> orders = new ArrayList<>();
        for(Map.Entry<String,String> sortParam :sorts.entrySet()){
            String sortField = sortParam.getKey();
            Sort.Direction sortDir = Sort.Direction.fromString(sortParam.getValue());
            if(Arrays.stream(Friend.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(sortField))){
                orders.add(new Sort.Order(sortDir,sortField));
            }
            else {
                throw CommonException.builder().message("Неправильно указано поле").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        return Sort.by(orders);
    }
    public void patchUser (String login){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        URI uri = UriComponentsBuilder.fromUriString(securityProps.getIntegrations().getUrl()).build(login);
        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .header("API_KEY", securityProps.getIntegrations().getApiKey())
                .build();
        try {
            ResponseEntity<Friend>  response = template.exchange(requestEntity, Friend.class );
            Friend body = response.getBody();
            List<Friend> friends = friendRepository.findAllByAddedFriendId(body.getId().toString());
            for (Friend friend: friends) {
                friend.setFirstName(body.getFirstName());
                friend.setSecondName(body.getSecondName());
                friend.setPatronymic(body.getPatronymic());
                friendRepository.save(friend);
        }
        }
        catch (HttpClientErrorException e){
            throw CommonException.builder().message(e.getMessage()).httpStatus(e.getStatusCode()).build();
        }
    }
    @Override
    public FriendPageDto findFriends(SearchFilterDto searchFilterDto){
        Map<String,String> filters = searchFilterDto.getFilters();
        filters.put("deleted","false");
        JwtUser jwtUserData =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        filters.put("mainId",jwtUserData.getId().toString() );
        searchFilterDto.setFilters(filters);
        Specification<Friend> filterSpec = createFilter(searchFilterDto.getFilters());
        PageRequest pageRequest = PageRequest.of(searchFilterDto.getPage(),searchFilterDto.getSize());
        Page<Friend> friendPage = friendRepository.findAll(filterSpec,pageRequest);
        List<FriendDtoForPage> friendDtos= friendPage.getContent().stream().map(this::toDto).toList();
        return new FriendPageDto(
                searchFilterDto.getPage(),
                friendDtos.size(),
                friendDtos
        );
    }
    public FriendDtoForPage toDto(Friend friend) {
        return new FriendDtoForPage(
                friend.getId(),
                friend.getAddTime(),
                friend.getDeleteTime(),
                friend.getAddedFriendId(),
                friend.getFirstName(),
                friend.getSecondName(),
                friend.getPatronymic()
        );
    }
}
