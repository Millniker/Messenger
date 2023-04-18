package com.user;

import com.common.security.provider.JwtSettingsProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "security.jwt-token")
@ConstructorBinding
@Validated
public class JwtSettings implements JwtSettingsProvider {

    private final String secret;
    private final long expirationMinutes;

    @Override
    public long getExpirationMinutes() {
        return expirationMinutes;
    }
}
