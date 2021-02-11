package com.app.service.exceptions;


/**
 * Exception thrown when entered invalid data
 */
public class WrongDataFormatException extends Exception {

    public WrongDataFormatException(String message) {
        super(message);
    }

    public WrongDataFormatException() {
        super("File does not have required format!");
    }
}
