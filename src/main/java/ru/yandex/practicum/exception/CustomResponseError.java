package ru.yandex.practicum.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CustomResponseError {

    private final String message;
    private final String exceptionMessage;
}
