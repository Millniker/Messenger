package com.friends.service.impl;

import com.common.model.CreateNotifyDTO;
import com.common.model.NotifyType;
import com.common.security.props.SecurityProps;
import com.common.service.JwtService;
import com.friends.entity.DTO.CheckBanDto;
import com.friends.entity.DTO.CheckFriendDto;
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
import com.friends.service.BannedFriendService;
import com.friends.service.FriendsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final FriendsMupper friendsMupper;
    private final FriendRepository friendRepository;
    private final SecurityProps securityProps;
    private final BannedFriendService bannedFriendService;
    private final StreamBridge streamBridge;
    private final JwtService jwtService;


    private final Clock clock;

    /**
     *
     * Принимает поля Id друга, Фио и добавляет его в друзья
     */
    @Override
    @Transactional
    public void addFriends(AddFriendDto addFriendDto){
        Friend oldFriend = friendRepository.findByMainIdAndAddedFriendId(jwtService.getCurrentUserId().toString(),addFriendDto.getAddedFriendId());
        if(Objects.equals(addFriendDto.getAddedFriendId(), jwtService.getCurrentUserId().toString())){
            throw CommonException.builder().message("Вы не можете добавить самого себя в друзья").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        if(bannedFriendService.isBanned(new CheckBanDto(jwtService.getCurrentUserId(),UUID.fromString(addFriendDto.getAddedFriendId())))){
            throw CommonException.builder().message("Пользователь добавил вас в черный список").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        if(oldFriend!=null){
            if (oldFriend.getDeleteTime()!=null){
                oldFriend.setDeleteTime(null);
            }
            else {
                throw CommonException.builder().message("Пользователь уже добавлен в друзья").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        Friend friend = friendsMupper.toEntity(addFriendDto);
        friend.setAddTime(LocalDateTime.now(clock));
        friend.setAddedFriendId(addFriendDto.getAddedFriendId());
        friend.setMainId(jwtService.getCurrentUserId().toString());
        friend.setDeleted("false");
        Friend addedfriend= friendsMupper.toEntity(addFriendDto);
        addedfriend.setAddTime(LocalDateTime.now(clock));
        addedfriend.setMainId(addFriendDto.getAddedFriendId());
        addedfriend.setAddedFriendId(jwtService.getCurrentUserId().toString());
        sendByStreamBridge(new CreateNotifyDTO(
                UUID.fromString(addFriendDto.getAddedFriendId()),
                NotifyType.NewFriend,
                "Пользователь "+jwtService.getCurrentUserId()+" добавил вас в друзья"
        ));
        friendRepository.save(friend);
        friendRepository.save(addedfriend);
    }

    /**
     *
     * Принимает id пользователя и удаляет его из друзей
     */
    @Override
    @Transactional
    public void deleteFriend(UUID id){
        Friend friend = friendRepository.findByMainIdAndAddedFriendId(jwtService.getCurrentUserId().toString(),id.toString());
        Friend addedFriend = friendRepository.findByAddedFriendIdAndMainId(jwtService.getCurrentUserId().toString(),id.toString());
        if(friend==null){
            throw CommonException.builder().message("Пользователь не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        if(addedFriend==null){
            throw CommonException.builder().message("Пользователь не является вашим другом").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        friend.setDeleteTime(LocalDateTime.now(clock));
        friend.setDeleted("true");
        addedFriend.setDeleteTime(LocalDateTime.now(clock));
        addedFriend.setDeleted("true");
        sendByStreamBridge(new CreateNotifyDTO(
                UUID.fromString(friend.getAddedFriendId()),
                NotifyType.NewFriend,
                "Пользователь"+friend.getMainId()+"добавил вас в друзья"
        ));
        friendRepository.save(addedFriend);
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
        filters.put("mainId",jwtService.getCurrentUserId().toString() );
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
    @Override
    public Boolean isFriend(CheckFriendDto checkFriendDto) {
        return friendRepository.existsByMainIdAndAddedFriendId(checkFriendDto.getMainId().toString(), checkFriendDto.getUserId().toString());
    }

    @Override
    public FriendPageDto findFriends(SearchFilterDto searchFilterDto){
        if(searchFilterDto.getSize()==0){
            throw CommonException.builder().message("Size должен быть больше 0").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        Map<String,String> filters = searchFilterDto.getFilters();
        filters.put("deleted","false");
        filters.put("mainId",jwtService.getCurrentUserId().toString() );
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
    private void sendByStreamBridge(CreateNotifyDTO notifyDTO) {
        streamBridge.send("NewNotify-out-0", notifyDTO);
    }
}
