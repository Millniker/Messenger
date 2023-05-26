package com.notify.mapper;


import com.notify.entity.DTO.CreateNotifyDTO;
import com.notify.entity.DTO.NotifyDto;
import com.notify.entity.Notify;

public interface NotifyMupper {

        NotifyDto toDto(Notify notify);
        Notify toEntity(CreateNotifyDTO notifyDTO);

}

