package com.friends.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;
@Data
@AllArgsConstructor
public class SearchFilterDto {
    @NotBlank
    int page=0;
    @NotBlank
    int size =1;
    @NotBlank
    private Map<String,String> filters;
}
