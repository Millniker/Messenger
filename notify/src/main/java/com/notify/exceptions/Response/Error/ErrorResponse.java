package com.notify.exceptions.Response.Error;


import com.notify.exceptions.Response.Response;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ErrorResponse implements Response {
    private Error error;
}