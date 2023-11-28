package ru.yandex.practicum.exception;

public class CustomValidationException extends RuntimeException {
    public CustomValidationException(String s) {
        super(s);
    }
}
