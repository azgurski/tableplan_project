package com.zgurski.exception;

public class InvalidInputValueException extends RuntimeException {
    public InvalidInputValueException() {
        super();
    }

    public InvalidInputValueException(String message) {
        super(message);
    }
}