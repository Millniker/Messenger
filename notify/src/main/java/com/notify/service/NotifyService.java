package com.notify.service;

import com.notify.entity.DTO.ChangeStatusDto;
import com.notify.entity.DTO.CreateNotifyDTO;
import com.notify.entity.DTO.PageNotifyDTO;
import com.notify.entity.DTO.SearchNotifyDto;

public interface NotifyService {
    void addNotify(CreateNotifyDTO notifyDTO);
    PageNotifyDTO filterNotifications(SearchNotifyDto filterDTO);
    Integer getUnreadMessages ();
    void changeNotifyStatus(ChangeStatusDto changeStatusDto);

}
