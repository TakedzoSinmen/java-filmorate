package ru.yandex.practicum.storage.daoImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.model.enums.FindBy;
import ru.yandex.practicum.model.enums.SortBy;
import ru.yandex.practicum.storage.api.FilmStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDaoStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        Number key = simpleJdbcInsert.executeAndReturnKey(filmToMap(film));
        film.setId((Integer) key);
        genreParamsAdd(film);
        directorParamsAdd(film);
        log.debug("Film with ID {} saved.", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String query = "UPDATE Film SET film_name=?, description=?, release_date=?, duration=?, rate =?, mpa_id=? " +
                "WHERE film_id=?";
        int updateResult = jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        if (updateResult > 0) {
            log.debug("Film with ID {} has been updated.", film.getId());
        } else {
            throw new EntityNotFoundException("Film not founded for update by ID=" + film.getId());
        }
        genreParamsUpdate(film);
        directorParamsUpdate(film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.rate," +
                "mpa.mpa_id, mpa.mpa_name " +
                "FROM Film as f " +
                "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id ";
        return jdbcTemplate.query(sqlQuery, mapToFilm());
    }

    @Override
    public Film getFilmById(Integer id) {
        try {
            String sqlQuery = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.rate, " +
                    "mpa.mpa_id, mpa.mpa_name " +
                    "FROM Film as f " +
                    "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
                    "WHERE film_id = ?";
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
                    "f.rate, mpa.mpa_id, mpa.mpa_name " +
                    "FROM FILM f " +
                    "JOIN DIRECTOR_FILM df ON f.film_id = df.film_id " +
                    "JOIN DIRECTOR d ON df.director_id = d.director_id " +
                    "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
                    "WHERE df.director_id = ? " +
                    "ORDER BY f.release_date";

            return jdbcTemplate.query(query, mapToFilm(), directorId);
        }
        if (sortBy.equals(SortBy.LIKES)) {
            String query = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                    "f.rate, mpa.mpa_id, mpa.mpa_name " +
                    "FROM FILM f " +
                    "LEFT JOIN Like_Film lf ON f.film_id = lf.film_id " +
                    "JOIN Director_Film df ON df.film_id = f.film_id " +
                    "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
                    "WHERE df.director_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT (lf.user_id)";

            return jdbcTemplate.query(query, mapToFilm(), directorId);
        }
        log.error("Incorrect parameter: {}", sortBy);
        throw new EntityNotFoundException(String.format("Incorrect parameter: %s", sortBy));
    }

    @Override
    public List<Film> searchFilmsByOneParameter(String query, FindBy param) {
        switch (param) {
            case TITLE:
                String sqlByTitle = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration," +
                        "mpa.mpa_id, mpa.mpa_name " +
                        "FROM film f " +
                        "LEFT JOIN LIKE_FILM lf ON f.film_id = lf.film_id " +
                        "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
                        "WHERE REPLACE(LOWER(film_name), ' ', '') LIKE LOWER ('%" + query + "%') " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(lf.like_id) DESC";

                return jdbcTemplate.query(sqlByTitle, mapToFilm());
            case DIRECTOR:
                String sqlByDirector = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration," +
                        "mpa.mpa_id, mpa.mpa_name " +
                        "FROM FILM f " +
                        "JOIN director_film df ON f.film_id = df.film_id " +
                        "JOIN director d ON d.director_id = df.director_id " +
                        "JOIN like_film lf ON lf.film_id = f.film_id " +
                        "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
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
    public List<Film> searchFilmsByBothParameters(String query, List<FindBy> params) {
        if (FindBy.TITLE.equals(params.get(0)) && FindBy.DIRECTOR.equals(params.get(1)) ||
                FindBy.TITLE.equals(params.get(1)) && FindBy.DIRECTOR.equals(params.get(0))) {
            String sqlByBothParams = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration," +
                    "mpa.mpa_id, mpa.mpa_name " +
                    "FROM FILM f " +
                    "LEFT JOIN director_film df ON f.film_id = df.film_id " +
                    "LEFT JOIN director d ON d.director_id = df.director_id " +
                    "LEFT JOIN like_film lf ON lf.film_id = f.film_id " +
                    "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
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
                "f.rate, mpa.mpa_id, mpa.mpa_name, COUNT(lf.user_id) AS likes " +
                "FROM Film AS f " +
                "JOIN Like_Film AS lf ON f.film_id = lf.film_id " +
                "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
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
                " f.release_date, f.duration, mpa.mpa_id, mpa.mpa_name " +
                "FROM Film AS f " +
                "LEFT JOIN Like_Film AS lf ON f.film_id = lf.film_id " +
                "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id ");
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

    @Override
    public void load(List<Film> films) {
        if (films.isEmpty()) {
            log.info("пустой список фильмов");
            return;
        }
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        Object[] filmIds = films.stream().map(Film::getId).toArray();

        final String sqlQueryGenres = "SELECT gf.film_id, g.genre_id, g.genre_name " +
                "FROM Genre_Film AS gf " +
                "JOIN Genre AS g ON gf.genre_id = g.genre_id " +
                "WHERE gf.film_id IN (" + inSql + ")";
        jdbcTemplate.query(sqlQueryGenres, mapToFilmUsingGenres(filmById), filmIds);

        final String sqlQueryDirectors = "SELECT df.film_id, d.director_id, d.director_name " +
                "FROM Director_Film AS df " +
                "JOIN Director AS d ON df.director_id = d.director_id " +
                "WHERE df.film_id IN (" + inSql + ")";
        jdbcTemplate.query(sqlQueryDirectors, mapToFilmUsingDirectors(filmById), filmIds);


        final String sqlQueryRate = "SELECT film_id, COUNT(user_id) AS rate " +
                "FROM Like_Film " +
                "WHERE film_id IN (" + inSql + ") " +
                "GROUP BY film_id";
        log.info("{}", jdbcTemplate.query(sqlQueryRate, mapToFilmUsingRate(filmById), filmIds));
    }

    private RowMapper<Film> mapToFilmUsingGenres(Map<Integer, Film> filmById) {
        return (rs, rowNum) -> {
            Film film = filmById.get(rs.getInt("film_id"));
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            film.addGenre(genre);
            return film;
        };
        /*
        private List<Genre> insertGenres(int filmId) {
        String query = "SELECT g.genre_id, g.genre_name " +
                "FROM Genre g " +
                "JOIN Genre_Film gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ?";

        List<Genre> list = jdbcTemplate.query(query, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        ), filmId).stream().distinct().collect(Collectors.toList());
        Set<Genre> set = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        for (Genre g : list) {
            set.add(g);
        }
        return new ArrayList<>(set);
    }
         */
    }

    private RowMapper<Film> mapToFilmUsingDirectors(Map<Integer, Film> filmById) {
        return (rs, rowNum) -> {
            Film film = filmById.get(rs.getInt("film_id"));
            Director director = Director.builder()
                    .id(rs.getInt("director_id"))
                    .name(rs.getString("director_name"))
                    .build();
            film.addDirector(director);
            return film;
        };
    }

    private RowMapper<Film> mapToFilmUsingRate(Map<Integer, Film> filmById) {
        return (rs, rowNum) -> {
            Film film = filmById.get(rs.getInt("film_id"));
            film.setRate(rs.getInt("rate"));
            return film;
        };
    }

    private RowMapper<Film> mapToFilm() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
            return film;
        };
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

    private void directorParamsAdd(Film film) {
        if (!film.getDirectors().isEmpty()) {
            String directorQuery = "INSERT INTO Director_Film (film_id, director_id) VALUES (?,?)";
            List<Object[]> directorParams = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                directorParams.add(new Object[]{film.getId(), director.getId()});
            }
            jdbcTemplate.batchUpdate(directorQuery, directorParams);
        }
    }

    private void genreParamsAdd(Film film) {
        if (!film.getGenres().isEmpty()) {
            String genreQuery = "INSERT INTO Genre_Film (film_id, genre_id) VALUES (?,?)";
            List<Object[]> genreParams = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                genreParams.add(new Object[]{film.getId(), genre.getId()});
            }
            jdbcTemplate.batchUpdate(genreQuery, genreParams);
        }
    }

    private void genreParamsUpdate(Film film) {
        if (!film.getGenres().isEmpty()) {
            String querySql = "DELETE FROM Genre_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, film.getId());
            String insertGenreQuery = "INSERT INTO Genre_Film (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> genreParams = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                genreParams.add(new Object[]{film.getId(), genre.getId()});
            }
            jdbcTemplate.batchUpdate(insertGenreQuery, genreParams);

        } else {
            String querySql = "DELETE FROM Genre_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, film.getId());
        }
    }

    private void directorParamsUpdate(Film film) {
        if (!film.getDirectors().isEmpty()) {
            String querySql = "DELETE FROM Director_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, film.getId());
            String insertDirectorQuery = "INSERT INTO Director_Film (film_id, director_id) VALUES (?, ?)";
            film.setDirectors(new HashSet<>(film.getDirectors()));
            List<Object[]> directorParams = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                directorParams.add(new Object[]{film.getId(), director.getId()});
            }
            jdbcTemplate.batchUpdate(insertDirectorQuery, directorParams);
        } else {
            String querySql = "DELEtE FROM Director_Film WHERE film_id =?";
            jdbcTemplate.update(querySql, film.getId());
        }
    }
}