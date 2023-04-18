package com.common.security;

import com.common.model.JwtUser;
import com.common.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.ap.shaded.freemarker.template.utility.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationConverter  implements AuthenticationConverter {
    private static final String AUTHORIZATION_SCHEME_BEARER = "Bearer";
    private final JwtService jwtService;
    @Override
    public Authentication convert(HttpServletRequest request) {
        String header= request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header==null){
            return null;
        }
        header = header.trim();
        if(!StringUtils.startsWithIgnoreCase(header,AUTHORIZATION_SCHEME_BEARER)){
            return null;
        }
        if(header.equalsIgnoreCase(AUTHORIZATION_SCHEME_BEARER)){
            throw new BadCredentialsException("Empty bearer authentication token");
        }
        String token = header.substring(AUTHORIZATION_SCHEME_BEARER.length()+1);
        if(!jwtService.validateToken(token)){
            throw new BadCredentialsException("Invalid bearer authentication token");
        }
        JwtUser user = jwtService.parseUser(token);
        return UsernamePasswordAuthenticationToken.authenticated(user,null, Collections.emptyList());
    }
}
