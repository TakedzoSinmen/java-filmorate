package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.enums.FindBy;
import ru.yandex.practicum.model.enums.SortBy;
import ru.yandex.practicum.storage.api.FilmStorage;
import ru.yandex.practicum.storage.api.LikeStorage;
import ru.yandex.practicum.storage.api.UserStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;
    @Mock
    private LikeStorage likeStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private EventService eventService;

    @InjectMocks
    private FilmService filmService;

    @Test
    void testLikeWhenFilmAndUserExistThenReturnTrue() {
        Integer filmId = 1;
        Integer userId = 1;

        assertTrue(filmService.like(filmId, userId));

        verify(likeStorage).like(filmId, userId);
        verify(eventService).addEvent(any(Event.class));
    }

    @Test
    void testUnlikeWhenFilmAndUserExistThenNoException() {
        Integer filmId = 1;
        Integer userId = 1;
        when(filmStorage.getFilmById(filmId)).thenReturn(new Film());
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(new User()));

        filmService.unlike(filmId, userId);

        verify(likeStorage).unLike(filmId, userId);
        verify(userStorage).getUserById(userId);
        verify(filmStorage).getFilmById(filmId);
        verify(eventService).addEvent(any(Event.class));
    }

    @Test
    void testGetFilmsThenReturnFilms() {
        List<Film> expectedFilms = Arrays.asList(new Film(), new Film());
        when(filmStorage.getFilms()).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.getFilms();

        assertEquals(expectedFilms, actualFilms);
        verify(filmStorage).getFilms();
    }

    @Test
    void testAddFilmThenReturnFilm() {
        Film film = new Film();
        when(filmStorage.addFilm(film)).thenReturn(film);

        Film result = filmService.addFilm(film);

        assertEquals(film, result);
        verify(filmStorage).addFilm(film);
    }

    @Test
    void testUpdateFilmThenReturnFilm() {
        Film film = new Film();
        when(filmStorage.updateFilm(film)).thenReturn(film);

        Film result = filmService.updateFilm(film);

        assertEquals(film, result);
        verify(filmStorage).updateFilm(film);
    }

    @Test
    void testGetFilmByIdThenReturnFilm() {
        Integer filmId = 1;
        Film expectedFilm = new Film();
        when(filmStorage.getFilmById(filmId)).thenReturn(expectedFilm);

        Film actualFilm = filmService.getFilmById(filmId);

        assertEquals(expectedFilm, actualFilm);
        verify(filmStorage).getFilmById(filmId);
    }

    @Test
    void testDeleteFilmByIdThenNoException() {
        Integer filmId = 1;
        doNothing().when(filmStorage).deleteFilmById(filmId);

        assertDoesNotThrow(() -> filmService.deleteFilmById(filmId));
        verify(filmStorage).deleteFilmById(filmId);
    }

    @Test
    void testGetFilmsByDirectorIdSortByThenReturnFilms() {
        Integer directorId = 1;
        SortBy variable = SortBy.YEAR;
        List<Film> expectedFilms = Arrays.asList(new Film(), new Film());
        when(filmStorage.getFilmsByDirectorIdSortBy(variable, directorId)).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.getFilmsByDirectorIdSortBy(directorId, variable);

        assertEquals(expectedFilms, actualFilms);
        verify(filmStorage).getFilmsByDirectorIdSortBy(variable, directorId);
    }

    @Test
    void testSearchFilmsByParamsWhenOneParamThenReturnFilms() {
        String query = "query";
        List<FindBy> params = List.of(FindBy.TITLE);
        List<Film> expectedFilms = Arrays.asList(new Film(), new Film());
        when(filmStorage.searchFilmsByOneParameter(query, params.get(0))).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.searchFilmsByParams(query, params);

        assertEquals(expectedFilms, actualFilms);
        verify(filmStorage).searchFilmsByOneParameter(query, params.get(0));
    }

    @Test
    void testSearchFilmsByParamsWhenTwoParamsThenReturnFilms() {
        String query = "query";
        List<FindBy> params = List.of(FindBy.TITLE, FindBy.DIRECTOR);
        List<Film> expectedFilms = Arrays.asList(new Film(), new Film());
        when(filmStorage.searchFilmsByBothParameters(query, params)).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.searchFilmsByParams(query, params);

        assertEquals(expectedFilms, actualFilms);
        verify(filmStorage).searchFilmsByBothParameters(query, params);
    }

    @Test
    void testGetFriendCommonFilmsThenReturnFilms() {
        Integer userId = 1;
        Integer friendId = 2;
        List<Film> expectedFilms = Arrays.asList(new Film(), new Film());
        when(filmStorage.getFriendCommonFilms(userId, friendId)).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.getFriendCommonFilms(userId, friendId);

        assertEquals(expectedFilms, actualFilms);
        verify(filmStorage).getFriendCommonFilms(userId, friendId);
    }

    @Test
    void testGetTopFilmWithFilterThenReturnFilms() {
        Integer count = 10;
        Integer genreId = 1;
        Integer year = 2021;
        List<Film> expectedFilms = Arrays.asList(new Film(), new Film());
        when(filmStorage.getTopFilmWithFilter(count, genreId, year)).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.getTopFilmWithFilter(count, genreId, year);

        assertEquals(expectedFilms, actualFilms);
        verify(filmStorage).getTopFilmWithFilter(count, genreId, year);
    }
}