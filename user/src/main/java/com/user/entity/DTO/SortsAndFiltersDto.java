package com.user.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@AllArgsConstructor
public class SortsAndFiltersDto {
    @NotBlank
    int page=0;
    @NotBlank
    int size =10;
    @NotBlank
    private Map<String,String> filters;
    @NotBlank
    private Map<String,String> sorts;
}
