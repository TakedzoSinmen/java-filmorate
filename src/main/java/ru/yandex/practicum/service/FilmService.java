package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.FindBy;
import ru.yandex.practicum.model.enums.Operation;
import ru.yandex.practicum.model.enums.SortBy;
import ru.yandex.practicum.storage.api.EventStorage;
import ru.yandex.practicum.storage.api.FilmStorage;
import ru.yandex.practicum.storage.api.LikeStorage;
import ru.yandex.practicum.storage.api.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public boolean like(Integer filmId, Integer userId) {
        likeStorage.like(filmId, userId);
        eventStorage.addEvent(Event.builder()
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
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build()
        );
    }

    public List<Film> getFilms() {
        List<Film> result = filmStorage.getFilms();
        filmStorage.load(result);
        return result;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);
        filmStorage.load(List.of(film));
        return film;
    }

    public void deleteFilmById(Integer id) {
        filmStorage.deleteFilmById(id);
    }

    public List<Film> getFilmsByDirectorIdSortBy(Integer directorId, SortBy variable) {
        List<Film> result = filmStorage.getFilmsByDirectorIdSortBy(variable, directorId);
        filmStorage.load(result);
        return result;
    }

    public List<Film> searchFilmsByParams(String query, List<FindBy> params) {
        switch (params.size()) {
            case 1:
                List<Film> resultOneParam = filmStorage.searchFilmsByOneParameter(query, params.get(0));
                filmStorage.load(resultOneParam);
                return resultOneParam;

            case 2:
                List<Film> resultBothParam = filmStorage.searchFilmsByBothParameters(query, params);
                filmStorage.load(resultBothParam);
                return resultBothParam;

            default:
                throw new EntityNotFoundException("Что то не так с параметрами");
        }
    }

    public List<Film> getFriendCommonFilms(Integer userId, Integer friendId) {
        List<Film> result = filmStorage.getFriendCommonFilms(userId, friendId);
        filmStorage.load(result);
        return result;
    }

    public List<Film> getTopFilmWithFilter(Integer count, Integer genreId, Integer year) {
        List<Film> result = filmStorage.getTopFilmWithFilter(count, genreId, year);
        filmStorage.load(result);
        return result;
    }
}