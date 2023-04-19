package com.user.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SortsAndFiltersDto {
    int page=0;
    int size =10;
    private Map<String,String> filters;
    private Map<String,String> sorts;
}
