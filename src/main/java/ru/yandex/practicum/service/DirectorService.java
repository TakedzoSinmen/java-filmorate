package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.storage.api.DirectorStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director getDirectorById(Integer id) {
        return directorStorage.getById(id);
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAll();
    }

    public Director createDirector(Director director) {
        return directorStorage.create(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateById(director);
    }

    public void deleteDirectorById(Integer id) {
        directorStorage.deleteById(id);
    }

}
