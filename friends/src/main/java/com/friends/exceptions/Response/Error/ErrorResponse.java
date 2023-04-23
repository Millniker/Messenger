package com.friends.exceptions.Response.Error;


import com.friends.exceptions.Response.Response;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ErrorResponse implements Response {
    private Error error;
}