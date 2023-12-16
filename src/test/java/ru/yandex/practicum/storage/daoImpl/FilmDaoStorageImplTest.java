package ru.yandex.practicum.storage.daoImpl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.api.*;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeStorageImpl.class, UserDaoStorageImpl.class})
public class FilmDaoStorageImplTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    @MockBean
    private final LikeStorage likeStorage;
    @MockBean
    private final MpaStorage mpaStorage;

    private final UserStorage userStorage;
    private Film film;

    @BeforeEach
    void set() {
        GenreStorage genreStorage = new GenreStorageImpl(jdbcTemplate);
        filmStorage = new FilmDaoStorageImpl(jdbcTemplate, genreStorage, likeStorage, mpaStorage);
        FilmService filmService = new FilmService(filmStorage, likeStorage, userStorage);

        film = new Film(0, "Film1", "descFilm1", LocalDate.of(2023, 1, 1), 100, 1, Collections.emptyList(), new Mpa(1, "G"));
    }

    @Test
    @DirtiesContext
    void shouldAddFilm() {
        set();
        Film savedFilm = filmStorage.addFilm(film);

        assertThat(savedFilm).isNotNull().isEqualTo(film);
    }


    @Test
    @DirtiesContext
    void shouldCreateFilmWithId1() {
        set();
        Film savedFilm = filmStorage.addFilm(film);

        assertThat(savedFilm.getId()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void shouldUpdateFilm() {
        set();
        Film film = new Film(1, "Film1", "descFilm1", LocalDate.of(2023, 1, 1), 100, 1, Collections.emptyList(), new Mpa(1, "G"));
        filmStorage.addFilm(film);

        film.setName("Updated Film");
        film.setDescription("Updated description");
        film.setDuration(120);

        Film updatedFilm = filmStorage.updateFilm(film);

        assertThat(updatedFilm).isNotNull().isEqualTo(film);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");
        assertThat(updatedFilm.getDuration()).isEqualTo(120);
    }
}