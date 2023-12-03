package ru.yandex.practicum.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomResponseError handleNotFound(final EntityNotFoundException exception) {
        return new CustomResponseError("сущность не найдена", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleBadRequest(final BadRequestException exception) {
        return new CustomResponseError("некорректный запрос", exception.getMessage());
    }
}
