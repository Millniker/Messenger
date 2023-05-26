package com.notify.config;

import com.notify.entity.DTO.CreateNotifyDTO;
import com.notify.entity.DTO.NotifyDto;
import com.notify.entity.Notify;
import com.notify.entity.enums.ReadStatus;
import com.notify.mapper.NotifyMupper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class NotifyMupperConfig {
    @Bean
    public NotifyMupper NotifyMapper() {
        return new NotifyMupper() {
            @Override
            public NotifyDto toDto(Notify notify) {
                return new NotifyDto(
                        notify.getId(),
                        notify.getType(),
                        notify.getText(),
                        notify.getReadStatus(),
                        notify.getSendDate()
                );
            }

            @Override
            public Notify toEntity(CreateNotifyDTO notifyDTO) {
                return new Notify(
                        UUID.randomUUID(),
                        notifyDTO.getType(),
                        notifyDTO.getText(),
                        notifyDTO.getUserId(),
                        ReadStatus.Unread,
                        null,
                        null
                );
            }
        };
    }
}
