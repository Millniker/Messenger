package com.notify.stream;

import com.notify.entity.DTO.CreateNotifyDTO;
import com.notify.service.impl.NotifyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class RabbitEventListener {
    private final NotifyServiceImpl notifyService;
    @Bean
    public Consumer<CreateNotifyDTO> NewNotify() {
        return notifyService::addNotify;
    }
}
