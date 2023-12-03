package ru.yandex.practicum.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.UserAlreadyLikedFilmException;
import ru.yandex.practicum.exception.UserDidntLikeFilmException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.impl.FilmStorage;
import ru.yandex.practicum.storage.impl.UserStorage;

import java.util.*;

@Service
@Slf4j
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private boolean isUserLikeAlreadyFilmChecker(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("Фильм с id: " + filmId + " не найден");
        }
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id: " + userId + " не найден");
        }
        if (film.getLikes().contains(user.getId())) {
            throw new UserAlreadyLikedFilmException("Пользователь с id: " + userId + " уже поставил лайк фильму: " + film);
        }
        return true;
    }

    public boolean like(Integer filmId, Integer userId) {
        if (isUserLikeAlreadyFilmChecker(filmId, userId)) {
            filmStorage.getFilmById(filmId).like(userId);
            return true;
        }
        return false;
    }

    public boolean unlike(Integer filmId, Integer userId) {
        if (!isUserLikeAlreadyFilmChecker(filmId, userId)) {
            filmStorage.getFilmById(filmId).getLikes().remove(userId);
            return true;
        } else {
            throw new UserDidntLikeFilmException("Пользователь с id: " + userId + " не ставил лайк фильму с id: " + filmId);
        }
    }

    public List<Film> getTopCountOr10Films(Integer count) {
        final Set<Film> top10Films = new TreeSet<>(Comparator.nullsLast((o1, o2) -> {
            if (o1.getLikes() != null && o2.getLikes() != null) {
                return Integer.compare(o1.getLikes().size(), o2.getLikes().size());
            } else if (o1 == o2) {
                return 0;
            } else {
                return 1;
            }
        }));
        top10Films.addAll(filmStorage.getFilms());
        List<Film> alreadyFilled = new ArrayList<>(top10Films);
        List<Film> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(alreadyFilled.get(i));
        }
        return result;
    }
}
