package edu.northeastern.cs5500.starterbot.exception;

public class PackageDoesnotExistException extends Exception {
    public PackageDoesnotExistException() {
        super();
    }

    public PackageDoesnotExistException(String message) {
        super(message);
    }
}
