package com.potato.balbambalbam.exception;

public class ResponseNotFoundException extends IllegalArgumentException{
    public ResponseNotFoundException(String message){
        super(message);
    }
}
