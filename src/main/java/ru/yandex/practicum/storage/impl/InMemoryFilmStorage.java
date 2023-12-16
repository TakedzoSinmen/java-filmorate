package ru.yandex.practicum.storage.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.api.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    private void generateId() {
        id++;
    }

    @Override
    public Film addFilm(Film film) {
        validateBody(film);
        film.setId(id);
        films.put(id, film);
        generateId();
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateBody(film);
        Film newFilm = films.get(film.getId());
        if (newFilm != null) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new CustomValidationException("Фильм не найден");
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new EntityNotFoundException("Фильма с указанным id: " + id + ", не найдено");
        }
        return films.get(id);
    }

    @Override
    public void deleteFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new EntityNotFoundException("Фильма с указанным id: " + id + ", не найдено");
        } else {
            films.remove(id);
        }
    }

    @Override
    public List<Film> getMostNLikedFilms(int count) {
        int countTopLikedFilms = count == 0 ? 10 : count;
        return films.values().stream()
                .sorted()
                .limit(countTopLikedFilms)
                .collect(Collectors.toList());
    }

    private static void validateBody(Film film) throws CustomValidationException {
        if (film.getName().isEmpty()) {
            throw new CustomValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new CustomValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new CustomValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new CustomValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}