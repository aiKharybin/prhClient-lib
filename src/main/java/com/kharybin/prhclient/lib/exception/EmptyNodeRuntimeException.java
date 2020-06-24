package com.kharybin.prhclient.lib.exception;


//exception for absence of essential nodes
public class EmptyNodeRuntimeException extends PrhClientRuntimeException{

    public EmptyNodeRuntimeException(String message) {
        super(message);
    }
}
