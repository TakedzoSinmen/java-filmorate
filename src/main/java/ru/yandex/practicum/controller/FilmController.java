package ru.yandex.practicum.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.impl.FilmStorage;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean like(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        return filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean unLike(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        return filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopCountOr10Films(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getTopCountOr10Films(count);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") Integer id) {
        return filmStorage.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilmById(@PathVariable(value = "id") Integer id) {
        return filmStorage.deleteFilmById(id);
    }
}
