package com.potato.balbambalbam.exception;

public class UserNotFoundException extends IllegalArgumentException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
