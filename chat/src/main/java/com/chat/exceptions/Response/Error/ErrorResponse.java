package com.chat.exceptions.Response.Error;


import com.chat.exceptions.Response.Response;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ErrorResponse implements Response {
    private Error error;
}