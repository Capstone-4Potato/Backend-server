package com.potato.balbambalbam.exception;

public class CategoryNotFoundException extends IllegalArgumentException{
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
