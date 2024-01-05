package ru.yandex.practicum.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.api.FilmStorage;
import ru.yandex.practicum.storage.api.LikeStorage;
import ru.yandex.practicum.storage.api.UserStorage;

import java.util.*;

@Service
@Slf4j
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDaoStorageImpl") FilmStorage filmStorage, LikeStorage likeStorage,
                       @Qualifier("userDaoStorageImpl") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
    }

    public boolean like(Integer filmId, Integer userId) {
        likeStorage.like(filmId, userId);
        return true;
    }

    public void unlike(Integer filmId, Integer userId) {
        if (userId < 1) {
            throw new EntityNotFoundException("User not exist");
        }
        if (filmId < 1) {
            throw new EntityNotFoundException("Film not exist");
        }
        getFilmById(filmId);
        userStorage.getUserById(userId);
        likeStorage.unLike(filmId, userId);
        log.debug("Удален лайк у фильма с  ID=" + filmId);
    }

    public List<Film> getTopCountOr10Films(Integer count) {
        return filmStorage.getMostNLikedFilms(count);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilmById(Integer id) {
        filmStorage.deleteFilmById(id);
    }

    // проверка осуществяется в следующем слое
    public List<Film> getFilmsByDirectorIdSortBy(Integer directorId, String variable) {
        return filmStorage.getFilmsByDirectorIdSortBy(variable, directorId);
    }

    public List<Film> getFriendCommonFilms(Integer userId, Integer friendId) {
        return filmStorage.getFriendCommonFilms(userId, friendId);
    }
}