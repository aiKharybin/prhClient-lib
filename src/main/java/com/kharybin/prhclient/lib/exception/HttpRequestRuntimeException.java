package com.kharybin.prhclient.lib.exception;

import org.springframework.http.HttpStatus;

//exception for all http client issues
public class HttpRequestRuntimeException extends PrhClientRuntimeException{

    final HttpStatus httpStatus;

    public HttpRequestRuntimeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
