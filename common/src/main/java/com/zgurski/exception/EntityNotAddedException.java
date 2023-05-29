package com.zgurski.exception;

public class EntityNotAddedException extends RuntimeException {
    public EntityNotAddedException(String message) {
        super(message);
    }
}