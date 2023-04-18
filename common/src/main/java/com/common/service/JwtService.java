package com.common.service;

import com.common.model.JwtUser;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Component;

@Component
public interface JwtService {
    String generateToken(@NotNull JwtUser jwtUser);
     JwtUser parseUser(@NotNull String token);
     boolean validateToken(@NotNull String token);
    }
