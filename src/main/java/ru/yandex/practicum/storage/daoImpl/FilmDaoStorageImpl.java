package ru.yandex.practicum.storage.daoImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.model.enums.SortBy;
import ru.yandex.practicum.storage.api.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
@Qualifier("filmDaoStorageImpl")
public class FilmDaoStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        Number key = simpleJdbcInsert.executeAndReturnKey(filmToMap(film));
        film.setId((Integer) key);
        if (!film.getGenres().isEmpty()) {
            String genreQuery = "INSERT INTO Genre_Film (film_id, genre_id) VALUES (?,?)";
            List<Object[]> genreParams = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                genreParams.add(new Object[]{film.getId(), genre.getId()});
            }
            jdbcTemplate.batchUpdate(genreQuery, genreParams);
        }
        if (!film.getDirectors().isEmpty()) {
            String directorQuery = "INSERT INTO Director_Film (film_id, director_id) VALUES (?,?)";
            List<Object[]> directorParams = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                directorParams.add(new Object[]{film.getId(), director.getId()});
            }
            jdbcTemplate.batchUpdate(directorQuery, directorParams);
        }
        log.debug("Film with ID {} saved.", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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
            log.debug("Film with ID {} has been updated.", filmId);
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
        if (!film.getDirectors().isEmpty()) {
            String querySql = "DELETE FROM Director_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, filmId);
            String insertDirectorQuery = "INSERT INTO Director_Film (film_id, director_id) VALUES (?, ?)";
            film.setDirectors(new HashSet<>(film.getDirectors()));
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(insertDirectorQuery, filmId, director.getId());
            }
        } else {
            String querySql = "DELEtE FROM Director_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, filmId);
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rate, mpa_id FROM Film";
        return jdbcTemplate.query(sqlQuery, mapToFilm());
    }

    @Override
    public Film getFilmById(Integer id) {
        try {
            String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rate, " +
                    "mpa_id FROM Film WHERE film_id = ?";
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
            log.debug("Film with ID {} has been removed.", id);
        } else {
            log.debug("Film with ID {} has not been deleted.", id);
        }
    }

    @Override
    public List<Film> getFilmsByDirectorIdSortBy(SortBy sortBy, int directorId) {
        isDirectorExist(directorId);
        if (sortBy.equals(SortBy.YEAR)) {
            String query = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                    "f.rate, f.mpa_id " +
                    "FROM FILM f " +
                    "JOIN DIRECTOR_FILM df ON f.film_id = df.film_id " +
                    "JOIN DIRECTOR d ON df.director_id = d.director_id " +
                    "WHERE df.director_id = ? " +
                    "ORDER BY f.release_date";

            return jdbcTemplate.query(query, mapToFilm(), directorId);
        }
        if (sortBy.equals(SortBy.LIKES)) {
            String query = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                    "f.rate, f.mpa_id " +
                    "FROM FILM f " +
                    "LEFT JOIN Like_Film lf ON f.film_id = lf.film_id " +
                    "JOIN Director_Film df ON df.film_id = f.film_id " +
                    "WHERE df.director_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT (lf.user_id)";

            return jdbcTemplate.query(query, mapToFilm(), directorId);
        }
        log.error("Incorrect parameter: {}", sortBy);
        throw new EntityNotFoundException(String.format("Incorrect parameter: %s", sortBy));
    }

    @Override
    public List<Film> searchFilmsByOneParameter(String query, String param) {
        switch (param) {
            case "title":
                String sqlByTitle = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id " +
                        "FROM film f " +
                        "LEFT JOIN LIKE_FILM lf ON f.film_id = lf.film_id " +
                        "WHERE REPLACE(LOWER(film_name), ' ', '') LIKE LOWER ('%" + query + "%') " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(lf.like_id) DESC";

                return jdbcTemplate.query(sqlByTitle, mapToFilm());
            case "director":
                String sqlByDirector = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id " +
                        "FROM FILM f " +
                        "JOIN director_film df ON f.film_id = df.film_id " +
                        "JOIN director d ON d.director_id = df.director_id " +
                        "JOIN like_film lf ON lf.film_id = f.film_id " +
                        "WHERE REPLACE(LOWER(d.director_name), ' ', '') LIKE LOWER ('%" + query + "%') " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(lf.like_id) DESC";
                return jdbcTemplate.query(sqlByDirector, mapToFilm());
            default:
                log.error("Неверно указан параметр {} ", param);
                throw new EntityNotFoundException(String.format("Неверно указан параметр %s ", param));
        }
    }

    @Override
    public List<Film> searchFilmsByBothParameters(String query, List<String> params) {
        String titleAndDirector = params.get(0) + "," + params.get(1);
        if (titleAndDirector.equals("title,director")
                || titleAndDirector.equals("director,title")) {
            String sqlByBothParams = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id " +
                    "FROM FILM f " +
                    "LEFT JOIN director_film df ON f.film_id = df.film_id " +
                    "LEFT JOIN director d ON d.director_id = df.director_id " +
                    "LEFT JOIN like_film lf ON lf.film_id = f.film_id " +
                    "WHERE REPLACE(LOWER(f.film_name), ' ', '') LIKE LOWER ('%" + query + "%') " +
                    "OR REPLACE(LOWER(d.director_name), ' ', '') LIKE LOWER ('%" + query + "%') " +
                    "GROUP BY f.film_id " +
                    "ORDER BY f.rate";

            return jdbcTemplate.query(sqlByBothParams, mapToFilm());
        }

        log.error("Неверно указаны параметры");
        throw new EntityNotFoundException("Неверно указаны параметры");
    }

    @Override
    public List<Film> getFriendCommonFilms(Integer userId, Integer friendId) {
        isUserExist(userId);
        isUserExist(friendId);
        String sql = "SELECT * FROM (SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "f.rate, f.mpa_id, COUNT(lf.user_id) AS likes " +
                "FROM Film AS f " +
                "JOIN Like_Film AS lf ON f.film_id = lf.film_id " +
                "WHERE lf.user_id = ?" +
                "GROUP BY f.film_id) AS ulf " +
                "JOIN " +
                "(SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "f.rate, f.mpa_id, COUNT(lf.user_id) AS likes " +
                "FROM Film AS f " +
                "JOIN Like_Film AS lf ON f.film_id = lf.film_id " +
                "WHERE lf.user_id = ? " +
                "GROUP BY f.film_id) AS flf ON ulf.film_id = flf.film_id " +
                "ORDER BY likes DESC";
        return jdbcTemplate.query(sql, mapToFilm(), userId, friendId);
    }

    @Override
    public List<Film> getTopFilmWithFilter(Integer count, Integer genreId, Integer year) {
        List<Film> topFilm = new ArrayList<>();
        String yearFilter = "WHERE YEAR(f.release_date) = ? ";
        String genreFilter = "WHERE gf.genre_id = ? ";
        String genreJoin = "JOIN Genre_Film AS gf ON f.film_id = gf.film_id ";
        String genreAndYearFilter = "WHERE gf.genre_id = ? AND YEAR(f.release_date) = ? ";
        String queryEnd = "GROUP BY f.film_id " +
                "ORDER BY COUNT(lf.like_id) " +
                "DESC LIMIT ?";
        StringBuilder query = new StringBuilder("SELECT f.film_id, f.film_name, f.description," +
                " f.release_date, f.duration, f.mpa_id " +
                "FROM Film AS f " +
                "LEFT JOIN Like_Film AS lf ON f.film_id = lf.film_id ");
        if (genreId == null && year == null) {
            String sqlString = query.append(queryEnd).toString();
            topFilm = jdbcTemplate.query(sqlString, mapToFilm(), count);
        }
        if (genreId == null && year != null) {
            String sqlString = query.append(yearFilter).append(queryEnd).toString();
            topFilm = jdbcTemplate.query(sqlString, mapToFilm(), year, count);
        }
        if (genreId != null && year == null) {
            String sqlString = query.append(genreJoin).append(genreFilter).append(queryEnd).toString();
            topFilm = jdbcTemplate.query(sqlString, mapToFilm(), genreId, count);
        }
        if (genreId != null && year != null) {
            String sqlString = query.append(genreJoin).append(genreAndYearFilter).append(queryEnd).toString();
            topFilm = jdbcTemplate.query(sqlString, mapToFilm(), genreId, year, count);
        }
        return topFilm;
    }

    @Override
    public void isUserExist(Integer userId) {
        String sql = "SELECT user_id FROM User_Filmorate WHERE user_id = ?";
        if (!jdbcTemplate.queryForRowSet(sql, userId).next()) {
            log.warn("User with id = {} was not found", userId);
            throw new EntityNotFoundException(String.format("User with id = %d was not found", userId));
        }
    }

    private void isDirectorExist(int id) {
        String sql = "select * from Director where director_id = ?";
        if (!jdbcTemplate.queryForRowSet(sql, id).next()) {
            log.warn("Director with id: {} was not found", id);
            throw new EntityNotFoundException(String.format("Director with id: %d was not found", id));
        }
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
                film.setRate(insertLikes(rs.getInt("film_id")).size());
                film.setMpa(insertMpa(rs.getInt("mpa_id")).get());
                film.setGenres(insertGenres(film.getId()));
                film.setDirectors(insertDirectors(film.getId()));
                return film;
            }
        };
    }

   private List<Integer> insertLikes(Integer filmId) {
        String query = "SELECT user_id FROM Like_Film WHERE film_id=?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query, filmId);
        List<Integer> likedUsers = new ArrayList<>();
        while (sqlRowSet.next()) {
            likedUsers.add(sqlRowSet.getInt("user_id"));
        }
        return likedUsers;
    }

    private Optional<Mpa> insertMpa(int id) {
        String query = "SELECT mpa_id, mpa_name FROM Mpa WHERE mpa_id=?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(query,
                    (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private List<Genre> insertGenres(int filmId) {
        String query = "SELECT g.genre_id, g.genre_name " +
                "FROM Genre g " +
                "JOIN Genre_Film gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        ), filmId);
    }

    private Set<Director> insertDirectors(Integer filmId) {
        String sql = "SELECT d.director_id, d.director_name " +
                "FROM Director_Film df " +
                "JOIN Director d ON df.director_id = d.director_id " +
                "WHERE df.film_id = ?";
        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> {
            int directorId = rs.getInt("director_id");
            String directorName = rs.getString("director_name");
            if (directorId == 0) {
                throw new EntityNotFoundException("Director not found for id: " + directorId);
            }
            return Director.builder()
                    .id(directorId)
                    .name(directorName)
                    .build();
        }, filmId);
        return new HashSet<>(directors);
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }
}