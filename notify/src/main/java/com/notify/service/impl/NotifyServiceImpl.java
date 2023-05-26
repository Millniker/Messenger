package com.notify.service.impl;

import com.common.model.JwtUser;
import com.notify.entity.DTO.*;
import com.notify.entity.Notify;
import com.notify.entity.enums.ReadStatus;
import com.notify.exceptions.CommonException;
import com.notify.mapper.NotifyMupper;
import com.notify.repository.NotifyRepository;
import com.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {
    private final NotifyRepository notifyRepository;
    private final NotifyMupper notifyMupper;
    private final EntityManager entityManager;

    private final Clock clock;

    /**
     *
     * добавление нового уведомления при получении через RabbitMq
     */
    @Transactional
    public void addNotify(CreateNotifyDTO notifyDTO){
        Notify notify = notifyMupper.toEntity(notifyDTO);
        notify.setSendDate(LocalDateTime.now(clock));
        notifyRepository.save(notify);
    }

    /**
     *
     * поиск с фильтром по уведомлениям
     */
    @Transactional(readOnly = true)
    public PageNotifyDTO filterNotifications(SearchNotifyDto filterDTO) {
        int startIndex = (filterDTO.getPage() - 1) *  filterDTO.getSize();
        String jpql = "SELECT n FROM Notify n WHERE userId=:user";
        JwtUser jwtUserData =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (filterDTO.getStartDate() != null) {
            jpql += " AND n.sendDate >= :startDate";
        }
        if (filterDTO.getEndDate() != null) {
            assert filterDTO.getStartDate() != null;
            if(!filterDTO.getStartDate().isBefore(filterDTO.getEndDate())){
                throw CommonException.builder().message("Дата окончания должна быть позже даты начала").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
            jpql += " AND n.sendDate <= :endDate";
        }
        if (StringUtils.hasText(filterDTO.getSearchText())) {
            jpql += " AND LOWER(n.text) LIKE LOWER(:searchText)";
        }
        if (filterDTO.getType()!=null) {
            jpql += " AND n.type IN :type";
        }
        TypedQuery<Notify> query = entityManager.createQuery(jpql, Notify.class);
        if (filterDTO.getStartDate() != null) {
            query.setParameter("startDate", filterDTO.getStartDate());
        }
        if (filterDTO.getEndDate() != null) {
            query.setParameter("endDate", filterDTO.getEndDate());
        }
        if (StringUtils.hasText(filterDTO.getSearchText())) {
            query.setParameter("searchText", "%" + filterDTO.getSearchText() + "%");
        }
        if (filterDTO.getType()!=null) {
            query.setParameter("type", filterDTO.getType());
        }
            query.setParameter("user", jwtUserData.getId());
        List<NotifyDto> notifyDtoList = query.getResultList().stream().map(NotifyDto::toDto).collect(Collectors.toList());
        Comparator<NotifyDto> comparator = Comparator.comparing(NotifyDto::getSendDate).reversed();
        notifyDtoList.sort(comparator);
        int endIndex = Math.min(startIndex +  filterDTO.getSize(),notifyDtoList.size());
        if(endIndex<startIndex){
            return new PageNotifyDTO(
                    filterDTO.getPage(),
                    filterDTO.getSize(),
                    new ArrayList<>());
        }

        return new PageNotifyDTO(
                filterDTO.getPage(),
                filterDTO.getSize(),
                notifyDtoList.subList(startIndex, endIndex));
    }

    /**
     *
     * получение количества сообщений
     */
    @Transactional(readOnly = true)
    public Integer getUnreadMessages (){
        JwtUser jwtUserData =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notifyRepository.findAllUnReadMessagesOptional(jwtUserData.getId()).size();
    }

    /**
     *
     * изменение состояния прочтения у уведомления
     */
    @Transactional
    public void changeNotifyStatus(ChangeStatusDto changeStatusDto){
        if(changeStatusDto.getNotify()!=null){
            for (UUID id:changeStatusDto.getNotify()
             ) {
                Notify notify = notifyRepository.findById(id).orElse(null);
                if(notify==null){
                    throw CommonException.builder().message("Уведомления"+id+"не существует").httpStatus(HttpStatus.NOT_FOUND).build();
                }
                if(changeStatusDto.getReadStatus()==ReadStatus.Read){
                    notify.setReadStatus(ReadStatus.Read);
                    notify.setReadedTime(LocalDateTime.now(clock));
                }
                else {
                    notify.setReadStatus(ReadStatus.Unread);
                    notify.setReadedTime(null);
                }
                notifyRepository.save(notify);
        }
    }
    }
}
