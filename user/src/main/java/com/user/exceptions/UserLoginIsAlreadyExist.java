package com.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserLoginIsAlreadyExist extends RuntimeException{
}
