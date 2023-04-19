package com.common.exceptions.Response.Error;


import com.common.exceptions.Response.Response;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ErrorResponse implements Response {
    private Error error;
}