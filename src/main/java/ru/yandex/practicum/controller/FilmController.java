package ru.yandex.practicum.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();

    private int id = 1;

    private void generateId() {
        id++;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateBody(film);
        film.setId(id);
        films.put(id, film);
        generateId();
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateBody(film);
        Film newFilm = films.get(film.getId());
        if (newFilm != null) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new CustomValidationException("Фильм не найден");
        }
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
