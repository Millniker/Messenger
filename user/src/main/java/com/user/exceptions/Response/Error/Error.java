package com.user.exceptions.Response.Error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class Error {
    private String message;
    private HttpStatus code;
}
