package com.common.service.impl;

import com.common.model.JwtUser;
import com.common.security.provider.JwtSettingsProvider;
import com.common.service.JwtService;
import com.sun.istack.NotNull;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
@Service
public class JwtServiceImpl implements JwtService {
    private final JwtSettingsProvider jwtSettings;
    private final SecretKey secretKey;
    private final Clock clock;
    public JwtServiceImpl(JwtSettingsProvider jwtSettings, Clock clock){
        this.jwtSettings = jwtSettings;
        this.clock =clock;
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSettings.getSecret()));
    }
    @Override
    public String generateToken(@NotNull JwtUser jwtUser){
        LocalDateTime now = LocalDateTime.now(clock);
        Long expMin =jwtSettings.getExpirationMinutes();
        Instant accessExpirationInstant = now.plusMinutes(jwtSettings.getExpirationMinutes()).atZone(ZoneId.systemDefault()).toInstant();
        Date expiration= Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(jwtUser.getId().toString())
                .setExpiration(expiration)
                .signWith(secretKey)
                .claim("username", jwtUser.getName())
                .compact();
    }
    @Override
    public boolean validateToken(@NotNull String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (JwtException ex){
            return false;
        }
    }
    @Override
    public JwtUser parseUser(@NotNull String token){
        Claims claims = getClaims(token);
        return new JwtUser(
                UUID.fromString(claims.getSubject()),
                claims.get("username",String.class)
        );
    }
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }


}
