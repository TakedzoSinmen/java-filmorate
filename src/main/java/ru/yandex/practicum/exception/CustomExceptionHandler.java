package ru.yandex.practicum.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomResponseError handleNotFound(final EntityNotFoundException exception) {
        log.debug("Получен статус {} : {}", HttpStatus.NOT_FOUND, exception.getMessage());
        return new CustomResponseError("сущность не найдена", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleBadRequest(final BadRequestException exception) {
        log.debug("Получен статус {} : {}", HttpStatus.BAD_REQUEST, exception.getMessage());
        return new CustomResponseError("некорректный запрос", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleBadRequest(final ConstraintViolationException exception) {
        log.debug("Получен статус {} : {}", HttpStatus.BAD_REQUEST, exception.getMessage());
        return new CustomResponseError("некорректный аргумент запроса", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleBadRequest(final ConversionFailedException exception) {
        log.debug("Получен статус {} : {}", HttpStatus.BAD_REQUEST, exception.getMessage());
        return new CustomResponseError("некорректный аргумент запроса", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.debug("Получен статус {} : {}", HttpStatus.BAD_REQUEST, exception.getMessage());
        return new CustomResponseError("неконсистентные данные",
                Objects.requireNonNull(exception.getFieldError()).getDefaultMessage());
    }
}