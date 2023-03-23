package edu.northeastern.cs5500.starterbot.exception;

public class NotFoundException extends RestException {
    public NotFoundException() {
        super("Not found", 404);
    }

    public NotFoundException(String message) {
        super(message, 404);
    }
}
