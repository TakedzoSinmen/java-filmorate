package ru.yandex.practicum.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.impl.FilmStorage;
import ru.yandex.practicum.storage.impl.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class FilmService {

    private static final Comparator<Film> COMPARATOR_LIKES = (curFilm, nextFilm) -> nextFilm.getLikes().size() - curFilm.getLikes().size();

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public boolean like(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        return true;
    }

    public boolean unlike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("фильма с id: " + filmId + " не найдено");
        }
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("пользователя с id: " + userId + " не найдено");
        }
        film.getLikes().remove(userId);
        return true;
    }

    public List<Film> getTopCountOr10Films(Integer count) {
        List<Film> result = filmStorage.getFilms();
        result.sort(COMPARATOR_LIKES);
        return result.stream().limit(count).collect(Collectors.toList());
    }
}
