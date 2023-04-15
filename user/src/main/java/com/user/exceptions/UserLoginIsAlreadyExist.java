package com.user.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus)
public class UserLoginIsAlreadyExist extends RuntimeException{
}
