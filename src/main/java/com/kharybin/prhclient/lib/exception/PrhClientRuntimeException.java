package com.kharybin.prhclient.lib.exception;


//This class is needed for more convenient try/catch block at users App side
public abstract class PrhClientRuntimeException extends RuntimeException {

    public PrhClientRuntimeException(String message) {
    }

    public PrhClientRuntimeException(String message, Throwable cause) {
    }

}
