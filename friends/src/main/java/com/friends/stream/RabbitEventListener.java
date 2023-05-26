package com.friends.stream;

import com.common.model.UpdateFIODto;
import com.friends.service.BanAndFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class RabbitEventListener {
    private final BanAndFriendService banAndFriendService;
    @Bean
    public Consumer<UpdateFIODto> UpdateUser() {
        return banAndFriendService::patchUser;
    }

}
