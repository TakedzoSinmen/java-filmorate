package ru.yandex.practicum.storage.daoImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.api.FilmStorage;
import ru.yandex.practicum.storage.api.GenreStorage;
import ru.yandex.practicum.storage.api.LikeStorage;
import ru.yandex.practicum.storage.api.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Qualifier("filmDaoStorageImpl")
@Slf4j
public class FilmDaoStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;

    private RowMapper<Film> mapToFilm() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
                Film film = new Film();
                film.setId(rs.getInt("film_id"));
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setRate(likeStorage.getLikesByFilmId(rs.getInt("film_id")).size());
                film.setMpa(mpaStorage.getMpaById(rs.getInt("mpa_id")).get());
                film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
                return film;
            }
        };
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("like_quantity", film.getRate());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }

    @Override
    public Film addFilm(Film film) {
        entityValidation(film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        Number key = simpleJdbcInsert.executeAndReturnKey(filmToMap(film));
        film.setId((Integer) key);
        if (!film.getGenres().isEmpty()) {
            String query = "INSERT INTO Genre_Film (film_id, genre_id) VALUES (?,?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(query, film.getId(), genre.getId());
            }
        }
        log.info("Film with ID {} saved.", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        entityValidation(film);
        int filmId = film.getId();
        String query = "UPDATE Film SET film_name=?, description=?, release_date=?, duration=?, rate =?, mpa_id=? " +
                "WHERE film_id=?";
        int updateResult = jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                filmId);
        if (updateResult > 0) {
            log.info("Film with ID {} has been updated.", filmId);
        } else {
            throw new EntityNotFoundException("Film not founded for update by ID=" + filmId);
        }
        if (!film.getGenres().isEmpty()) {
            String querySql = "DELETE FROM Genre_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, filmId);
            String insertGenreQuery = "INSERT INTO Genre_Film (film_id, genre_id) VALUES (?, ?)";
            film.setGenres(film.getGenres()
                    .stream()
                    .distinct()
                    .collect(Collectors.toList()));
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(insertGenreQuery, filmId, genre.getId());
            }
        } else {
            String querySql = "DELETE FROM Genre_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, filmId);
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM Film";
        return jdbcTemplate.query(sqlQuery, mapToFilm());
    }

    @Override
    public Film getFilmById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM Film WHERE film_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, mapToFilm(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Film not exist");
        }
    }

    @Override
    public void deleteFilmById(Integer id) {
        String query = "DELETE FROM Film WHERE film_id=?";
        int deleteResult = jdbcTemplate.update(query, id);
        if (deleteResult > 0) {
            log.info("Film with ID {} has been removed.", id);
        } else {
            log.info("Film with ID {} has not been deleted.", id);
        }
    }

    @Override
    public List<Film> getMostNLikedFilms(int count) {
        String query = "SELECT f.*, COUNT(lf.user_id) AS likes " +
                "FROM Film f " +
                "LEFT JOIN Like_Film lf ON f.film_id = lf.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes DESC " +
                "LIMIT ?";
        RowMapper<Film> filmRowMapper = mapToFilm();
        return jdbcTemplate.query(query, filmRowMapper, count);
    }

    private void entityValidation(Film film) {
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            log.warn("Ошибка при валидации фильма, не заполнено поле name= {}", film.getName());
            throw new BadRequestException("Ошибка при валидации фильма, не заполнено поле name= " + film.getName());
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка при валидации фильма, длина поля description >200, а именно= {}",
                    film.getDescription().length());
            throw new BadRequestException("Ошибка при валидации фильма, длина поля description >200, а именно= "
                    + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка при валидации фильма, дата релиза раньше 28.12.1895, а именно= {}",
                    film.getReleaseDate());
            throw new BadRequestException("Ошибка при валидации фильма, дата релиза раньше 28.12.1895, а именно= "
                    + film.getReleaseDate());
        }
        if (film.getDuration() < 0) {
            log.warn("Ошибка при валидации фильма, отрицательная продолжительность= {}", film.getDuration());
            throw new BadRequestException("Ошибка при валидации фильма, отрицательная продолжительность= "
                    + film.getDuration());
        }
    }
}