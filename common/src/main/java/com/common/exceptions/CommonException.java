package com.common.exceptions;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class CommonException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;
}