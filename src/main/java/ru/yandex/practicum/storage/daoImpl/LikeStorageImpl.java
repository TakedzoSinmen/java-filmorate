package ru.yandex.practicum.storage.daoImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.storage.api.LikeStorage;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
@Slf4j
public class LikeStorageImpl implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void like(Integer filmId, Integer userId) {
        String query = "INSERT INTO Like_Film (film_id, user_id) " +
                "SELECT ?, ? " +
                "WHERE NOT EXISTS (" +
                "SELECT 1 FROM Like_Film " +
                "WHERE film_id = ? AND user_id = ?)";
        int insertResult = jdbcTemplate.update(query, filmId, userId, filmId, userId);
    }

    @Override
    public ResponseEntity<String> unLike(Integer filmId, Integer userId) {
        if (userId < 1) {
            throw new EntityNotFoundException("User not exist");
        }
        if (filmId < 1) {
            throw new EntityNotFoundException("Film not exist");
        }
        String query = "DELETE FROM Like_Film WHERE film_id=? AND user_id=?";
        int deleteResult = jdbcTemplate.update(query, filmId, userId);
        if (deleteResult > 0) {
            log.info("User with ID {} has removed like for film by ID {}.", userId, filmId);
            return ResponseEntity.ok("Like removed successfully");
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nothing to delete here");
        }
    }

    @Override
    public List<Integer> getLikesByFilmId(Integer filmId) {
        String query = "SELECT user_id FROM Like_Film WHERE film_id=?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query, filmId);
        List<Integer> likedUsers = new ArrayList<>();
        while (sqlRowSet.next()) {
            likedUsers.add(sqlRowSet.getInt("user_id"));
        }
        return likedUsers;
    }
}