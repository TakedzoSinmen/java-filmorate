package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.api.MpaStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MpaServiceTest {

    @Mock
    private MpaStorage mpaStorage;

    @InjectMocks
    private MpaService mpaService;

    @Test
    void testGetMpaByIdWhenMpaExistsThenReturnMpa() {
        int mpaId = 1;
        Mpa expectedMpa = new Mpa(mpaId, "PG-13");
        when(mpaStorage.getMpaById(mpaId)).thenReturn(Optional.of(expectedMpa));

        Mpa actualMpa = mpaService.getMpaById(mpaId);

        assertEquals(expectedMpa, actualMpa);
    }

    @Test
    void testGetMpaByIdWhenMpaDoesNotExistThenThrowEntityNotFoundException() {
        int mpaId = 99;
        when(mpaStorage.getMpaById(mpaId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> mpaService.getMpaById(mpaId),
                "Expected getMpaById to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Mpa not exist by id=" + mpaId));
    }

    @Test
    void testGetAllMpa() {
        List<Mpa> expectedMpas = Arrays.asList(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13")
        );
        when(mpaStorage.getAllMpa()).thenReturn(expectedMpas);

        List<Mpa> actualMpas = mpaService.getAllMpa();

        assertEquals(expectedMpas, actualMpas);
    }
}