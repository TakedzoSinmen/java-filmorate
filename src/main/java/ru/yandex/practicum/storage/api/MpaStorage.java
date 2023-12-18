package ru.yandex.practicum.storage.api;

import ru.yandex.practicum.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    Optional<Mpa> getMpaById(int id);

    List<Mpa> getAllMpa();
}