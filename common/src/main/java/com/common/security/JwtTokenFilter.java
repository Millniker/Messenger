package com.common.security;

import com.common.model.JwtUser;
import com.common.security.exception.UnauthorizedException;
import com.sun.istack.NotNull;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.common.security.SecurityConst.HEADER_JWT;


/**
 * Фильтр аутентификации по JWT.
 * Если запрос попадает в этот фильтр, то запрос обязан содержать корректный JWT токен, иначе фильтр вернёт 401 статус
 */
@RequiredArgsConstructor
class JwtTokenFilter extends OncePerRequestFilter {

    private final String secretKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var jwt = request.getHeader(HEADER_JWT);
        jwt = jwt.trim();
        jwt = jwt.substring("Bearer".length()+1);
        if(!validateToken(jwt)){
            throw new BadCredentialsException("Invalid bearer authentication token");
        }
        // парсинг токена
        JwtUser jwtUser;
        try {
            jwtUser =parseUser(jwt);
        } catch (JwtException e) {
            // может случиться, если токен протух или некорректен
            throw new UnauthorizedException();
        }

        var authentication = new JwtAuthentication(jwtUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
    public JwtUser parseUser(@NotNull String token){
        Claims claims = getClaims(token);
        return new JwtUser(
                UUID.fromString(claims.getSubject()),
                claims.get("username",String.class),
                claims.get("login",String.class)
        );
    }
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
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
}
