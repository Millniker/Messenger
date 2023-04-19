package com.common.security;

import com.common.model.JwtUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Данные {@link org.springframework.security.core.Authentication} по запросам с UI.
 * В качестве principal и details - {@link JwtUser}
 */
public class JwtAuthentication extends AbstractAuthenticationToken {

    public JwtAuthentication(JwtUser jwtUserData) {
        super(null);
        this.setDetails(jwtUserData);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getDetails();
    }

}
