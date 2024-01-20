package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.api.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Mpa getMpaById(int id) {
        try {
            return mpaStorage.getMpaById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Mpa not exist by id=" + id));
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Mpa not exist by id=" + id);
        }
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}