package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.api.InMemoryFilmStorage;
import ru.yandex.practicum.storage.api.InMemoryUserStorage;
import ru.yandex.practicum.storage.impl.FilmStorage;
import ru.yandex.practicum.storage.impl.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    Film film;
    Film film2;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
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
        Executable executable = () -> filmController.addFilm(film);

        assertThrows(CustomValidationException.class, executable);
    }

    @Test
    void givenWrongFilm_whenPutRequest_thenThrowException() {
        Executable executable = () -> filmController.updateFilm(film);

        assertThrows(CustomValidationException.class, executable);
    }

    @Test
    void givenRightFilm_whenUpdateFilm_thenMapValueUpdated() {
        filmController.addFilm(film2);

        Film newFilm = new Film(222, "", "", LocalDate.now(), 222);
        newFilm.setId(film2.getId());
        newFilm.setName("Друзья");
        newFilm.setDescription("Ромком в классическом стиле");
        newFilm.setReleaseDate(LocalDate.of(1998, 7, 5));
        newFilm.setDuration(160);

        filmController.updateFilm(newFilm);

        assertEquals(filmController.getFilms().get(0), newFilm);
    }
}