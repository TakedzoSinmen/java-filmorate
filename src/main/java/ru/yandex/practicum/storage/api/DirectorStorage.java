package ru.yandex.practicum.storage.api;

import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.model.Film;

import java.util.List;

public interface DirectorStorage {

    List<Director> getAll();

    Director getById(Integer id);

    Director create(Director director);

    Director updateById(Director director);

    void deleteById(Integer id);

}
