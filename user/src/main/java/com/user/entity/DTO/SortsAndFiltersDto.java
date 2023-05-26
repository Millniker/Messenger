package com.user.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@AllArgsConstructor
public class SortsAndFiltersDto {
    @NotBlank
    public int page=0;
    @NotBlank
    public int size =10;
    @NotBlank
    public Map<String,String> filters;
    @NotBlank
    public Map<String,String> sorts;
}
