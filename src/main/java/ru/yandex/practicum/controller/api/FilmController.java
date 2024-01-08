package ru.yandex.practicum.controller.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        log.debug("GET request received to receive all films");
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("POST request received to create new film");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("PUT request received to update film by given entity");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean like(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT request received to add new like to film with id= {} by user with id= {}", id, userId);
        return filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unLike(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT request received to add new like to film with id= {} by user with id= {}", id, userId);
        filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopCountOr10Films(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.debug("GET request received to get top 10 films");
        return filmService.getTopCountOr10Films(count);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") Integer id) {
        log.debug("GET request received to get film by given id= {}", id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFilmById(@PathVariable(value = "id") Integer id) {
        log.debug("DELETE request received to delete film by given id= {}", id);
        filmService.deleteFilmById(id);
        return ResponseEntity.ok("Film with ID " + id + " has been successfully deleted");
    }

    // Новый контроллер для получение всех фильмов определенного режиссёра
    // отсортированных по лайкам или году релиза
    // В url приходит айдишник желаемого режиссёра и параметр в виде строки = (like / year)
    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectorId(@PathVariable Integer directorId,
                                           @RequestParam String sortBy) {

        return filmService.getFilmsByDirectorIdSortBy(directorId, sortBy);
    }

    // Получаем строку "query" -> содержащую текст, по которому искать совпадения
    // а так же List с параметрами (ожидаем title,director или director,title);
    @GetMapping("/search")
    public List<Film> getSearchFilmsByParams(@RequestParam String query,
                                             @RequestParam List<String> by) {

        return filmService.searchFilmsByParams(query, by);
    }

    @GetMapping("/common")
    public List<Film> getFriendCommonFilms(@RequestParam(name = "userId") Integer userId,
                                           @RequestParam(name = "friendId") Integer friendId) {
        log.debug("GET request received to get films shared with a friend sorted by their popularity using " +
                "userId = {} and friendId = {}", userId, friendId);
        return filmService.getFriendCommonFilms(userId, friendId);
    }
}