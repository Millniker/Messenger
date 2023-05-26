package com.notify.exceptions;


import com.notify.exceptions.Response.Error.Error;
import com.notify.exceptions.Response.Error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> notFoundException(CommonException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder()
                .message(ex.getMessage())
                .code(ex.getHttpStatus())
                .build()).build(),ex.getHttpStatus());
    }
}
