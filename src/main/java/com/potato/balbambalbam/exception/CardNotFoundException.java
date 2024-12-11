package com.potato.balbambalbam.exception;

public class CardNotFoundException extends IllegalArgumentException{
    public CardNotFoundException(String s) {
        super(s);
    }
}
