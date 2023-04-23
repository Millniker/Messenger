package com.friends.entity.DTO.FriendDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendDto {
    private UUID id;
    private LocalDateTime addTime;
    private LocalDateTime deleteTime;
    @NotBlank
    private String mainId;
    @NotBlank
    private String addedFriendId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String secondName;
    @NotBlank
    private String patronymic;
}
