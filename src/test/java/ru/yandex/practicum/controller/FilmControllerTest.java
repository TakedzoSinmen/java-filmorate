package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    Film film;
    Film film2;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = new Film(22, "", "", LocalDate.of(1777, 9, 2), -1);
        film2 = new Film(1, "Марко Поло", "Драмеди про похождения друзей",
                LocalDate.of(2000, 10, 8), 135);

    }

    @Test
    void givenRightFilm_whenAddFilm_thenMapFilled() {
        filmController.addFilm(film2);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void givenRightFilm_whenGetFilms_thenGetListOfFilms() {
        filmController.addFilm(film2);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void givenWrongFilm_whenPostRequest_thenThrowException() {
        assertThrows(CustomValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void givenWrongFilm_whenPutRequest_thenThrowException() {
        assertThrows(CustomValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void givenRightFilm_whenUpdateFilm_thenMapValueUpdated() {
        filmController.addFilm(film2);

        Film newFilm = new Film();
        newFilm.setId(film2.getId());
        newFilm.setName("Друзья");
        newFilm.setDescription("Ромком в классическом стиле");
        newFilm.setReleaseDate(LocalDate.of(1998, 7, 5));
        newFilm.setDuration(160);

        filmController.updateFilm(newFilm);

        assertEquals(filmController.getFilms().get(0), newFilm);
    }
}