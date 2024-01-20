package ru.yandex.practicum.exception;

import lombok.Data;

@Data
public class CustomResponseError {
    private final String message;
    private final String exceptionMessage;
}