package ru.akarpo.openprofile.is_openprofile.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}