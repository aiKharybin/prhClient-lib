package com.kharybin.prhclient.lib.exception;

import org.springframework.http.HttpStatus;

public class HttpRequestRuntimeException extends RuntimeException{

    final HttpStatus httpStatus;

    public HttpRequestRuntimeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
