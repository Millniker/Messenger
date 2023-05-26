package com.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFIODto {
    public UUID id;
    public String firstname;
    public String seconfname;
    public String patronomicname;
}
