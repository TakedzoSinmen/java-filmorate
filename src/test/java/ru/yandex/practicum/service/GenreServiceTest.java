package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.api.GenreStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreStorage genreStorage;

    @InjectMocks
    private GenreService genreService;

    @Test
    void givenValidId_whenGetGenreById_thenReturnGenre() {
        Integer genreId = 1;
        Genre expectedGenre = new Genre(genreId, "Comedy");
        when(genreStorage.getById(genreId)).thenReturn(Optional.of(expectedGenre));

        Genre actualGenre = genreService.getGenreById(genreId);

        assertEquals(expectedGenre, actualGenre);
    }

    @Test
    void givenInvalidId_whenGetGenreById_thenThrowEntityNotFoundException() {
        Integer genreId = 999;
        when(genreStorage.getById(genreId)).thenReturn(Optional.empty());
        Executable executable = () -> genreService.getGenreById(genreId);

        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void whenGetAll_thenReturnListOfGenres() {
        List<Genre> expectedGenres = Arrays.asList(
                new Genre(1, "Comedy"),
                new Genre(2, "Drama")
        );
        when(genreStorage.getAll()).thenReturn(expectedGenres);

        List<Genre> actualGenres = genreService.getAll();

        assertEquals(expectedGenres, actualGenres);
    }

    @Test
    void givenFilmId_whenGetGenresByFilmId_thenReturnListOfGenres() {
        Integer filmId = 1;
        List<Genre> expectedGenres = Arrays.asList(
                new Genre(1, "Comedy"),
                new Genre(2, "Drama")
        );
        when(genreStorage.getGenresByFilmId(filmId)).thenReturn(expectedGenres);

        List<Genre> actualGenres = genreService.getGenresByFilmId(filmId);

        assertEquals(expectedGenres, actualGenres);
    }
}