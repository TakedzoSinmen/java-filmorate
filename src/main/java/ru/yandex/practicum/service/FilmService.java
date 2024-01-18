package ru.yandex.practicum.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.Operation;
import ru.yandex.practicum.model.enums.SortBy;
import ru.yandex.practicum.storage.api.FilmStorage;
import ru.yandex.practicum.storage.api.LikeStorage;
import ru.yandex.practicum.storage.api.UserStorage;

import java.util.List;

@Service
@Slf4j
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    public FilmService(@Qualifier("filmDaoStorageImpl") FilmStorage filmStorage, LikeStorage likeStorage,
                       @Qualifier("userDaoStorageImpl") UserStorage userStorage, EventService eventService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public boolean like(Integer filmId, Integer userId) {
        likeStorage.like(filmId, userId);
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build()
        );
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
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build()
        );
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

    public List<Film> getFilmsByDirectorIdSortBy(Integer directorId, SortBy variable) {
        return filmStorage.getFilmsByDirectorIdSortBy(variable, directorId);
    }

    // проверяем количество параметров в листе и уходим в соответсвующий метод
    public List<Film> searchFilmsByParams(String query, List<String> params) {

        switch (params.size()) {
            case 1:
                return filmStorage.searchFilmsByOneParameter(query, params.get(0));

            case 2:
                return filmStorage.searchFilmsByBothParameters(query, params);

            default:
                throw new EntityNotFoundException("Что то не так с параметрами");
        }
    }

    public List<Film> getFriendCommonFilms(Integer userId, Integer friendId) {
        return filmStorage.getFriendCommonFilms(userId, friendId);
    }

    public List<Film> getTopFilmWithFilter(Integer count, Integer genreId, Integer year) {
        return filmStorage.getTopFilmWithFilter(count, genreId, year);
    }
}