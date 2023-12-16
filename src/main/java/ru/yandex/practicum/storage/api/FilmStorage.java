package ru.yandex.practicum.storage.api;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(Integer id);

    void deleteFilmById(Integer id);

    List<Film> getMostNLikedFilms(int count);
}