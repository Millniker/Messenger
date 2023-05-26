package com.chat.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class PageDto {
    @NotBlank
    int page=0;
    @NotBlank
    int size =50;
    String chatName;
}
