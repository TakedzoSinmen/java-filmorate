package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.api.FilmStorage;
import ru.yandex.practicum.storage.api.UserStorage;
import ru.yandex.practicum.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;
    private UserService userService;
    private Film film;
    private Film film2;
    private Film wrongFilm;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        userService = new UserService(userStorage);
        film = new Film(1, "погоня", "описание погони", LocalDate.of(2001, 9, 13), 222);
        film2 = new Film(2, "побег", "описание побега", LocalDate.of(2001, 9, 12), 444);
        wrongFilm = new Film(999, "", "", LocalDate.of(1777, 7, 7), -2000);
    }

    @Test
    void givenFilmEntity_whenTryToAddFilm_thenFilmStorageMustSaveEntity() {
        filmService.addFilm(film);
        filmService.addFilm(film2);

        assertEquals(2, filmService.getFilms().size());
    }

    @Test
    void givenWrongFilm_whenTryToAddFilmEntity_thenMustThrowException() {
        Executable executable = () -> filmService.addFilm(wrongFilm);

        assertThrows(CustomValidationException.class, executable);
    }

    @Test
    void givenNewEntity_whenTryToUpdateEntityInStorage_thenEntityReplaceWithNewOne() {
        filmService.addFilm(film);
        filmService.addFilm(film2);

        assertEquals(film, filmService.getFilmById(film.getId()));

        Film newFilm = new Film(1, "newName", "newDescription", LocalDate.of(2015, 10, 9), 120);
        filmService.updateFilm(newFilm);

        assertEquals(newFilm.getReleaseDate(), filmService.getFilms().get(0).getReleaseDate());
    }

    @Test
    void whenTryToAddLike_thenLikeListSizeMustGrowUp() {
        userService.addUser(new User(1, "s@s.s", "s", "s", LocalDate.of(1990, 3, 4)));
        filmService.addFilm(film);
        filmService.addFilm(film2);
        filmService.like(1, 1);

        assertEquals(1, film.getLikes().size());
    }
}