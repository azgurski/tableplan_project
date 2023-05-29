package com.zgurski.exception;

public class EntityIncorrectOwnerException extends RuntimeException {
    public EntityIncorrectOwnerException(String message) {
        super(message);
    }
}