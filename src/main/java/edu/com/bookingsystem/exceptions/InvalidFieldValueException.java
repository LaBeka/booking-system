package edu.com.bookingsystem.exceptions;

public class InvalidFieldValueException extends RuntimeException {
    public InvalidFieldValueException(String message) {
        super(message);
    }
}
