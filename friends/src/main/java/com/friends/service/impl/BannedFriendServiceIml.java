package com.friends.service.impl;

import com.common.model.CreateNotifyDTO;
import com.common.model.NotifyType;
import com.common.security.props.SecurityProps;
import com.common.service.JwtService;
import com.friends.entity.BannedFriend;
import com.friends.entity.DTO.BannedFriendDto.AddBannedFriendsDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendDto;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendDtoForPage;
import com.friends.entity.DTO.BannedFriendDto.BannedFriendPageDto;
import com.friends.entity.DTO.CheckBanDto;
import com.friends.entity.DTO.SearchFilterDto;
import com.friends.entity.DTO.SortsAndFiltersDto;
import com.friends.entity.Friend;
import com.friends.exceptions.CommonException;
import com.friends.mapper.FriendsMupper;
import com.friends.repository.BannedFriendRepository;
import com.friends.service.BannedFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BannedFriendServiceIml implements BannedFriendService {
    private final FriendsMupper bannedFriendMapper;
    private final BannedFriendRepository bannedFriendRepository;
    private final SecurityProps securityProps;
    private final StreamBridge streamBridge;
    private final JwtService jwtService;


    /**
     *
     * Принимает поля Id друга, Фио и добавляет его в черный список
     */

    @Override
    @Transactional
    public void addBanFriend(AddBannedFriendsDto addBannedFriendsDto){
        BannedFriend oldFriend = bannedFriendRepository.findByMainIdAndAddedFriendId(jwtService.getCurrentUserId().toString(),addBannedFriendsDto.getAddedFriendId());
        if(oldFriend!=null){
            if (oldFriend.getDeleteTime()!=null){
                oldFriend.setDeleteTime(null);
            }
            else {
                throw CommonException.builder().message("Пользователь уже добавлен в черный список").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        BannedFriend bannedFriend = bannedFriendMapper.toBanEntity(addBannedFriendsDto);
        bannedFriend.setAddTime(LocalDateTime.now());
        bannedFriend.setAddedFriendId(addBannedFriendsDto.getAddedFriendId());
        bannedFriend.setMainId(jwtService.getCurrentUserId().toString());
        bannedFriend.setDeleted("false");
        sendByStreamBridge(new CreateNotifyDTO(
                UUID.fromString(bannedFriend.getAddedFriendId()),
                NotifyType.RemoveFromFriends,
                "Пользователь "+jwtService.getCurrentUserId()+"добавил вас в черный список"
        ));
        bannedFriendRepository.save(bannedFriend);
    }
    /**
     *
     * Принимает id пользователя и удаляет его из черного списка
     */
    @Override
    @Transactional
    public void deleteBanFriend(UUID id){
        BannedFriend bannedFriend = bannedFriendRepository.findByAddedFriendId(id.toString());
        if(bannedFriend==null){
            throw CommonException.builder().message("Пользователь не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        bannedFriend.setDeleteTime(LocalDateTime.now());
        bannedFriend.setDeleted("true");
        sendByStreamBridge(new CreateNotifyDTO(
                UUID.fromString(bannedFriend.getAddedFriendId()),
                NotifyType.RemoveFromFriends,
                "Пользователь"+bannedFriend.getMainId()+"удалил вас из черного списка"
        ));
        bannedFriendRepository.save(bannedFriend);
    }

    /**
     *получение забаненого пользователя по id
     */
    @Transactional(readOnly = true)
    @Override
    public BannedFriendDto getUserById(UUID id){
        BannedFriend bannedFriend = bannedFriendRepository.findByAddedFriendId(id.toString());
        if(bannedFriend==null){
            throw CommonException.builder().message("Пользователь не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return bannedFriendMapper.toBanDto(bannedFriend);
    }

    /**
     *получение всех забаненых пользователей с фильтрацией
     */
    @Transactional(readOnly = true)
    @Override
    public BannedFriendPageDto getBanFriends (SortsAndFiltersDto sortsAndFiltersDto) {
        Map<String,String> filters = sortsAndFiltersDto.getFilters();
        filters.put("deleted","false");
        filters.put("mainId",jwtService.getCurrentUserId().toString() );
        sortsAndFiltersDto.setFilters(filters);
        Specification<BannedFriend> filterSpec = createFilter(sortsAndFiltersDto.getFilters());
        Sort sort = createSort(sortsAndFiltersDto.getSorts());
        PageRequest pageRequest = PageRequest.of(sortsAndFiltersDto.getPage(),sortsAndFiltersDto.getSize(),sort);
        Page<BannedFriend> bannedFriends = bannedFriendRepository.findAll(filterSpec,pageRequest);
        List<BannedFriendDtoForPage> bannedFriendDtoForPages= bannedFriends.getContent().stream().map(this::toDto).toList();
        return new BannedFriendPageDto(
                sortsAndFiltersDto.getPage(),
                bannedFriendDtoForPages.size(),
                bannedFriendDtoForPages
        );
    }

    /**
     *
     * поиск с фильтрами по забаненым пользователям
     *
     */
    @Transactional(readOnly = true)
    @Override
    public BannedFriendPageDto findBanFriends(SearchFilterDto searchFilterDto){
        if(searchFilterDto.getSize()==0){
            throw CommonException.builder().message("Size должен быть больше 0").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        Map<String,String> filters = searchFilterDto.getFilters();
        filters.put("deleted","false");
        filters.put("mainId",jwtService.getCurrentUserId().toString() );
        searchFilterDto.setFilters(filters);
        Specification<BannedFriend> filterSpec = createFilter(searchFilterDto.getFilters());
        PageRequest pageRequest = PageRequest.of(searchFilterDto.getPage(),searchFilterDto.getSize());
        Page<BannedFriend> bannedFriendPage = bannedFriendRepository.findAll(filterSpec,pageRequest);
        List<BannedFriendDtoForPage> bannedFriendDtoForPages= bannedFriendPage.getContent().stream().map(this::toDto).toList();
        return new BannedFriendPageDto(
                searchFilterDto.getPage(),
                bannedFriendDtoForPages.size(),
                bannedFriendDtoForPages
        );
    }
    private Specification<BannedFriend> createFilter(Map<String,String> filters){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for(Map.Entry<String,String> filter :filters.entrySet()){
                String filterField = filter.getKey();
                String filterValue = filter.getValue();
                if(Arrays.stream(BannedFriend.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(filterField))){
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

    public Boolean isBanned(CheckBanDto checkBanDto){
        BannedFriend bannedFriend = bannedFriendRepository.findByMainIdAndAddedFriendId(checkBanDto.getMainId().toString(),checkBanDto.getAddId().toString());
        if(bannedFriend!=null){
            return !Objects.equals(bannedFriend.getDeleted(), "true");
        }
        return false;
    }
    public BannedFriendDtoForPage toDto(BannedFriend bannedFriend) {
        return new BannedFriendDtoForPage(
                bannedFriend.getId(),
                bannedFriend.getAddTime(),
                bannedFriend.getDeleteTime(),
                bannedFriend.getAddedFriendId(),
                bannedFriend.getFirstName(),
                bannedFriend.getSecondName(),
                bannedFriend.getPatronymic()
        );
    }
    private void sendByStreamBridge(CreateNotifyDTO notifyDTO) {
        streamBridge.send("NewNotify-out-0", notifyDTO);
    }
}

