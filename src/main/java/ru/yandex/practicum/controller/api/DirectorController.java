package ru.yandex.practicum.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService service;

    @GetMapping("/{id}")
    public Director getById(@PathVariable(value = "id") Integer id) {
        log.info("GET -> get Director by id: {}", id);
        return service.getDirectorById(id);
    }

    @GetMapping
    public List<Director> getAll() {
        log.info("GET -> get all Directors");
        return service.getAllDirectors();
    }

    @PostMapping
    public Director create(@RequestBody @Valid Director director) {
        log.info("POST -> create Director");
        return service.createDirector(director);
    }

    @PutMapping
    public Director update(@RequestBody @Valid Director director) {
        log.info("PUT -> update Director");
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable(value = "id") Integer id) {
        log.info("DELETE -> delete Director with id: {}", id);
        service.deleteDirectorById(id);
    }

}
