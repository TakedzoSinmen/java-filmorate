package ru.yandex.practicum.storage.daoImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.storage.api.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoStorageImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private User mapToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name_user"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }


    private Map<String, Object> userToMap(User user) {
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("email", user.getEmail());
        userAttributes.put("login", user.getLogin());
        userAttributes.put("name_user", user.getName());
        userAttributes.put("birthday", user.getBirthday());
        return userAttributes;
    }

    @Override
    public List<User> getUsers() {
        String query = "SELECT user_id, email, login, name_user, birthday FROM User_Filmorate";
        log.debug("All users returned from DB");
        return jdbcTemplate.query(query, this::mapToUser);
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("User_Filmorate")
                .usingGeneratedKeyColumns("user_id");
        Number key = simpleJdbcInsert.executeAndReturnKey(userToMap(user));
        user.setId((Integer) key);
        log.debug("User with ID {} saved.", user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        String query = "SELECT user_id, email, login, name_user, birthday FROM User_Filmorate WHERE user_id=?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, this::mapToUser, id));
    }

    @Override
    public User updateUser(User user) {
        String query = "UPDATE User_Filmorate SET email=?, login=?, name_user=?, birthday=? WHERE user_id=?";
        int userId = user.getId();
        int updateResult = jdbcTemplate.update(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                userId);
        if (updateResult > 0) {
            log.debug("User with ID {} has been updated.", userId);
        } else {
            throw new EntityNotFoundException("User not founded for update by ID=" + userId);
        }
        return user;
    }

    @Override
    public void deleteUserById(Integer id) {
        String query = "DELETE FROM User_Filmorate WHERE user_id=?";
        int deleteResult = jdbcTemplate.update(query, id);
        if (deleteResult > 0) {
            log.info("User with ID {} has been removed.", id);
        } else {
            log.info("User with ID {} has not been deleted.", id);
        }
    }

    @Override
    public List<User> searchForUserFriends(int id) {
        String userExistQuery = "SELECT COUNT(user_id) FROM User_Filmorate WHERE user_id = ?";
        Integer userCount = jdbcTemplate.queryForObject(userExistQuery, Integer.class, id);
        if (userCount == null || userCount == 0) {
            log.debug("User with ID {} does not exist", id);
            throw new EntityNotFoundException("User not detected by ID=" + id);
        } else {
            String query = "SELECT uf.user_id, uf.email, uf.login, uf.name_user, uf.birthday " +
                    "FROM User_Filmorate uf " +
                    "JOIN Friendship f ON uf.user_id = f.friend_id " +
                    "WHERE f.user_id = ?";
            log.debug("All friends of user by ID {} returned from DB", id);
            return jdbcTemplate.query(query, this::mapToUser, id);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String query = "DELETE FROM Friendship WHERE user_id=? AND friend_id=?";
        int deleteResult = jdbcTemplate.update(query, userId, friendId);
        if (deleteResult > 0) {
            log.info("User with ID {} has been removed from friends of user by ID {}.", userId, friendId);
        } else {
            log.info("Users are not friends");
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new EntityNotFoundException("Users with same id not exists");
        }
        String query = "INSERT INTO Friendship (user_id, friend_id) " +
                "SELECT ?, ? " +
                "WHERE NOT EXISTS ( " +
                "SELECT 1 FROM Friendship " +
                "WHERE user_id = ? AND friend_id = ?)";
        int insertResult = jdbcTemplate.update(query, userId, friendId, userId, friendId);
        if (insertResult > 0) {
            log.debug("User with ID {} has been added in friends of user by ID {}.", friendId, userId);
        }
    }

    @Override
    public List<User> searchForSameFriends(int userId, int friendId) {
        List<User> commonFriends = new ArrayList<>();
        String query = "SELECT u.user_id, u.email, u.login, u.name_user, u.birthday FROM Friendship f1 " +
                "INNER JOIN Friendship f2 ON f1.friend_id = f2.friend_id " +
                "INNER JOIN User_Filmorate u ON f1.friend_id = u.user_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ? AND f1.friend_id = f2.friend_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query, userId, friendId);
        while (sqlRowSet.next()) {
            int id = sqlRowSet.getInt("user_id");
            commonFriends.add(getUserById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Common friend not exist in DB with ID=" + id)));
        }
        return commonFriends;
    }

    public List<Film> recommendations(Integer id) {
        try {
            String sql = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id,\n" +
                    "mpa.mpa_id, mpa.mpa_name\n" +
                    "FROM Film as f\n" +
                    "JOIN Like_Film as ml ON ml.film_id = f.film_id\n" +
                    "JOIN Mpa as mpa ON f.mpa_id = mpa.mpa_id " +
                    "WHERE ml.user_id IN (\n" +
                    "    SELECT DISTINCT ml2.user_id\n" +
                    "    FROM Like_Film as ml1\n" +
                    "    JOIN Like_Film as ml2 ON ml1.film_id = ml2.film_id AND ml1.user_id != ml2.user_id\n" +
                    "    WHERE ml1.user_id = ?\n" +
                    ")\n" +
                    "AND f.film_id NOT IN (\n" +
                    "    SELECT film_id\n" +
                    "    FROM Like_Film\n" +
                    "    WHERE user_id = ?\n" +
                    ")";
            return jdbcTemplate.query(sql, mapToFilm(), id, id);
        } catch (RuntimeException e) {
            log.debug("cant find recommendations to user with id = {}, try to change user id", id);
            throw new EntityNotFoundException("incorrect user id = " + id);
        }
    }

    @Override
    public void load(List<Film> films) {
        if (films.isEmpty()) {
            log.debug("No films to recommend");
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
}