package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private final FilmController filmController = new FilmController();

    Film film = new Film(22, "", "", LocalDate.of(1777, 9, 2), Duration.ZERO);

    @Test
    void givenWrongFilm_whenPostRequest_thenThrowException() {
        assertThrows(CustomValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void givenWrongFilm_whenPutRequest_thenThrowException() {
        assertThrows(CustomValidationException.class, () -> filmController.updateFilm(film));
    }
}