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
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.service.DirectorService;
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

@Slf4j
@Repository
@AllArgsConstructor
@Qualifier("filmDaoStorageImpl")
public class FilmDaoStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;
    private final DirectorService directorService;

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
                film.setDirectors(getDirectorsByFilmId(film.getId()));
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
        values.put("rate", film.getRate());
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
        // добавление режиссеров в базу (по аналогии с жанрами выше, т.к. логика аналогична)
        if (!film.getDirectors().isEmpty()) {
            String query = "INSERT INTO Director_Film (film_id, director_id) VALUES (?,?)";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(query, film.getId(), director.getId());
            }
        }

        log.debug("Film with ID {} saved.", film.getId());
        return film;
    }

    private List<Director> getDirectorsByFilmId(Integer filmId) {
        String sql = "SELECT director_id FROM Director_Film WHERE film_id = ?";
        List<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("director_id"), filmId);

        return ids.stream().map(directorService::getDirectorById).collect(Collectors.toList());
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

        // Обновление режиссёров фильма (не стал менять логику как выше, для прощей читаемости)
        if (!film.getDirectors().isEmpty()) {
            String querySql = "DELETE FROM Director_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, filmId);
            String insertDirectorQuery = "INSERT INTO Director_Film (film_id, director_id) VALUES (?, ?)";
            film.setDirectors(film.getDirectors()
                    .stream()
                    .distinct()
                    .collect(Collectors.toList()));
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
    public List<Film> getMostNLikedFilms(int count) {
        String query = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "f.rate, f.mpa_id, COUNT(lf.user_id) AS likes " +
                "FROM Film f " +
                "LEFT JOIN Like_Film lf ON f.film_id = lf.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes DESC " +
                "LIMIT ?";
        RowMapper<Film> filmRowMapper = mapToFilm();
        return jdbcTemplate.query(query, filmRowMapper, count);
    }

    // Проверка параметра sortBy и уже сама логика сортировки по лайкам или
    // году релиза в зависимости от параметра. В случае некоректно указанного параметра ->
    // выбрасываем исключение и сообщение пользователю в теле ответа
    @Override
    public List<Film> getFilmsByDirectorIdSortBy(String sortBy, int directorId) {
        isExist(directorId);
        String sortByYear = "year";
        String sortByLikes = "likes";

        if (sortBy.equals(sortByYear)) {

            String query = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                    "f.rate, f.mpa_id " +
                    "FROM FILM f " +
                    "JOIN DIRECTOR_FILM df ON f.film_id = df.film_id " +
                    "JOIN DIRECTOR d ON df.director_id = d.director_id " +
                    "WHERE df.director_id = ? " +
                    "ORDER BY f.release_date";

            return jdbcTemplate.query(query, mapToFilm(), directorId);
        }

        if (sortBy.equals(sortByLikes)) {

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

    private void isUserExist(Integer userId) {
        String sql = "SELECT user_id FROM User_Filmorate WHERE user_id = ?";
        if (!jdbcTemplate.queryForRowSet(sql, userId).next()) {
            log.warn("User with id = {} was not found", userId);
            throw new EntityNotFoundException(String.format("User with id = %d was not found", userId));
        }
    }

    // Проверка на существование фильма в базе по id
    private void isExist(int id) {
        String sql = "select * from Director where director_id = ?";
        if (!jdbcTemplate.queryForRowSet(sql, id).next()) {
            log.warn("Director with id: {} was not found", id);
            throw new EntityNotFoundException(String.format("Director with id: %d was not found", id));
        }
    }

    private void entityValidation(Film film) {
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            log.debug("Ошибка при валидации фильма, не заполнено поле name= {}", film.getName());
            throw new BadRequestException("Ошибка при валидации фильма, не заполнено поле name= " + film.getName());
        }
        if (film.getDescription().length() > 200) {
            log.debug("Ошибка при валидации фильма, длина поля description >200, а именно= {}",
                    film.getDescription().length());
            throw new BadRequestException("Ошибка при валидации фильма, длина поля description >200, а именно= "
                    + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Ошибка при валидации фильма, дата релиза раньше 28.12.1895, а именно= {}",
                    film.getReleaseDate());
            throw new BadRequestException("Ошибка при валидации фильма, дата релиза раньше 28.12.1895, а именно= "
                    + film.getReleaseDate());
        }
        if (film.getDuration() < 0) {
            log.debug("Ошибка при валидации фильма, отрицательная продолжительность= {}", film.getDuration());
            throw new BadRequestException("Ошибка при валидации фильма, отрицательная продолжительность= "
                    + film.getDuration());
        }
    }

}