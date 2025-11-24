package edu.com.bookingsystem.exceptions;

public class EntityDeactivatedException extends RuntimeException {
    public EntityDeactivatedException(String message) {
        super(message);
    }
}
