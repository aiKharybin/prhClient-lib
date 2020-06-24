package com.kharybin.prhclient.lib.exception;

//invalid json structure exception
public class JsonParsingRuntimeException extends PrhClientRuntimeException {

    public JsonParsingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
