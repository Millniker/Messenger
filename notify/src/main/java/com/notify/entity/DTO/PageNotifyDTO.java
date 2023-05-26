package com.notify.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageNotifyDTO {
    public int page;
    public int size;
    public List<NotifyDto> notifyDtoList;
}
