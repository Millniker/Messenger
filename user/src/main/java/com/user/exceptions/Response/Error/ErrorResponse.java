package com.user.exceptions.Response.Error;


import com.user.exceptions.Response.Response;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ErrorResponse implements Response {
    private Error error;
}