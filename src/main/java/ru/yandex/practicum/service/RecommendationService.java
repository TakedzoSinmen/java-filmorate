package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.api.GenreStorage;
import ru.yandex.practicum.storage.api.LikeStorage;
import ru.yandex.practicum.storage.api.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Так как реализовать алгоритм Slope One, без возможности пользователей ставтиь оценки фильмам,
 * не представляется возможным, мы будем обходиться одним методом запрашивающим из базы данных
 * информацию по пользователям с одинаковыми лайками, без построения матриц разностей и частот.
 */
@Slf4j
@Service
public class RecommendationService {

    private final JdbcTemplate jdbcTemplate;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorService directorService;

    @Autowired
    public RecommendationService(JdbcTemplate jdbcTemplate,
                                 LikeStorage likeStorage,
                                 MpaStorage mpaStorage,
                                 GenreStorage genreStorage,
                                 DirectorService directorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.likeStorage = likeStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorService = directorService;
    }

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
                film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId())));
                film.setDirectors(
                        jdbcTemplate.query("SELECT director_id FROM Director_Film WHERE film_id = ?",
                                        (resultSet, rowNumber) -> resultSet.getInt("director_id"), film.getId())
                                .stream()
                                .map(directorService::getDirectorById)
                                .collect(Collectors.toSet()));
                return film;
            }
        };
    }

    public List<Film> recommendations(Integer id) {
        try {
            String sql = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id\n" +
                    "FROM film as f\n" +
                    "JOIN like_film as ml ON ml.film_id = f.film_id\n" +
                    "WHERE ml.user_id IN (\n" +
                    "    SELECT DISTINCT ml2.user_id\n" +
                    "    FROM like_film as ml1\n" +
                    "    JOIN like_film as ml2 ON ml1.film_id = ml2.film_id AND ml1.user_id != ml2.user_id\n" +
                    "    WHERE ml1.user_id = ?\n" +
                    ")\n" +
                    "AND f.film_id NOT IN (\n" +
                    "    SELECT film_id\n" +
                    "    FROM like_film\n" +
                    "    WHERE user_id = ?\n" +
                    ")";
            return jdbcTemplate.query(sql, mapToFilm(), id, id);
        } catch (RuntimeException e) {
            log.debug("cant find recommendations to user with id= {}, try to change user id", id);
            throw new EntityNotFoundException("incorrect user id= " + id);
        }
    }
}