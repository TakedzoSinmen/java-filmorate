package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Genre;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreStorageImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private GenreStorageImpl genreStorage;

    private final Genre genre1 = new Genre(1, "Comedy");
    private final Genre genre2 = new Genre(2, "Drama");

    @BeforeEach
    void setUp() {
        reset(jdbcTemplate);
    }

    @Test
    void testGetByIdWhenGenreExistsThenReturnGenre() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(genre1);

        Optional<Genre> result = genreStorage.getById(1);

        assertTrue(result.isPresent());
        assertEquals(genre1, result.get());
    }

    @Test
    void testGetByIdWhenGenreDoesNotExistThenReturnEmptyOptional() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(null);

        Optional<Genre> result = genreStorage.getById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllWhenGenresExistThenReturnGenres() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(Arrays.asList(genre1, genre2));

        List<Genre> result = genreStorage.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(genre1, genre2)));
    }

    @Test
    void testGetAllWhenNoGenresThenReturnEmptyList() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<Genre> result = genreStorage.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetGenresByFilmIdWhenGenresExistThenReturnGenres() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(Arrays.asList(genre1, genre2));

        List<Genre> result = genreStorage.getGenresByFilmId(1);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(genre1, genre2)));
    }

    @Test
    void testGetGenresByFilmIdWhenNoGenresThenReturnEmptyList() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(Collections.emptyList());

        List<Genre> result = genreStorage.getGenresByFilmId(1);

        assertTrue(result.isEmpty());
    }
}