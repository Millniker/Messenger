package com.common.model;

import lombok.Data;

import java.security.Principal;
import java.util.UUID;

@Data
public class JwtUser implements Principal{
    private final UUID id;
    private final String login;
    @Override
    public String getName() {
        return login;
    }

}
