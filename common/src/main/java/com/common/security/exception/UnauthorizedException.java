package com.common.security.exception;

/**
 * Исключение аутентификации.
 * Его можно перехватывать в Controller Advice, чтобы возвращать 401 статус
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
