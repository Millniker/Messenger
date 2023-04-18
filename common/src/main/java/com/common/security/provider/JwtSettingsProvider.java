package com.common.security.provider;

public interface JwtSettingsProvider {
    String getSecret();
    long getExpirationMinutes();
}
