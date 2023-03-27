package edu.northeastern.cs5500.starterbot.exception;

import lombok.Getter;

public class RestException extends Exception {
    @Getter final int code;

    RestException(String message, int code) {
        super(message);
        this.code = code;
    }
}
