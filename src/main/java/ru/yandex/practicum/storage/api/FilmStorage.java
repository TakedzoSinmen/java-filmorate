package ru.yandex.practicum.storage.api;

import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.enums.SortBy;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(Integer id);

    void deleteFilmById(Integer id);

    List<Film> getFilmsByDirectorIdSortBy(SortBy sortBy, int directorId);

    List<Film> searchFilmsByOneParameter(String query, String param);

    List<Film> searchFilmsByBothParameters(String query, List<String> params);

    List<Film> getFriendCommonFilms(Integer userId, Integer friendId);

    List<Film> getTopFilmWithFilter(Integer count, Integer genreId, Integer year);

    void isUserExist(Integer userId);
}