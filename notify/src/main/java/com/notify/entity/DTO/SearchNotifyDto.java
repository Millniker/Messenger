package com.notify.entity.DTO;

import com.notify.entity.enums.NotifyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchNotifyDto {
    public int page=1;
    public int size=50;
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public String searchText;
    public NotifyType type;

}
