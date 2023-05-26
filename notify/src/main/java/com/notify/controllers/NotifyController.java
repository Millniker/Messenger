package com.notify.controllers;

import com.notify.entity.DTO.ChangeStatusDto;
import com.notify.entity.DTO.PageNotifyDTO;
import com.notify.entity.DTO.SearchNotifyDto;
import com.notify.service.impl.NotifyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotifyController {
    private final NotifyServiceImpl notifyService;
    @PostMapping("/search")
    public ResponseEntity<PageNotifyDTO> searchNotify(@RequestBody SearchNotifyDto searchNotifyDto){
        return ResponseEntity.ok(notifyService.filterNotifications(searchNotifyDto));
    }
    @GetMapping("/messages")
    public ResponseEntity<Integer> getUnreadMessages(){
        return ResponseEntity.ok(notifyService.getUnreadMessages());
    }
    @PutMapping("/status")
    public ResponseEntity<Void> changeStatus(@RequestBody ChangeStatusDto changeStatusDto){
        notifyService.changeNotifyStatus(changeStatusDto);
        return ResponseEntity.ok().build();
    }
}
