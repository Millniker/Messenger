package com.common.security;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JwtAuthenticationConverter authenticationConverter;
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try{
            Authentication authRequest = authenticationConverter.convert(request);
            if(authRequest ==null){
                filterChain.doFilter(request, response);
            }
            String login = authRequest.getName();
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authRequest);
            SecurityContextHolder.setContext(context);
        }
        catch (AuthenticationException ex){
            SecurityContextHolder.getContext();
        }

        filterChain.doFilter(request,response);
    }
}
