package ru.yandex.practicum.storage.daoImpl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Review;
import ru.yandex.practicum.storage.api.ReviewStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
@Repository
public class ReviewDaoStorageImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Review> mapToReview() {
        return (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }

    private Map<String, Object> reviewToMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        values.put("useful", review.getUseful());
        return values;
    }

    @Override
    public Review addReview(Review review) {
        entityValidation(review);
        forHandleReviewIdWithPostmanExceptionsFindUser(review.getUserId());
        forHandleReviewIdWithPostmanExceptionsFindFilm(review.getFilmId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("review_id");
        Number key = simpleJdbcInsert.executeAndReturnKey(reviewToMap(review));
        review.setReviewId((Integer) key);
        log.debug("Review with ID {} saved", review.getReviewId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {

        int reviewId = review.getReviewId();
        String query = "UPDATE Review SET content=?, is_positive=? WHERE review_id=?";
        boolean check = jdbcTemplate.update(query,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()) < 1;
        if (check) {
            throw new EntityNotFoundException("Review not founded for update, id = " + reviewId);
        }
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(Integer id) {
        String query = "DELETE FROM Review WHERE review_id=?";
        int deleteResult = jdbcTemplate.update(query, id);
        if (deleteResult > 0) {
            log.debug("Review with id= {} has been deleted", id);
        } else {
            log.debug("Review with id= {} has not been deleted", id);
        }
    }

    @Override
    public Review getReviewById(Integer id) {
        try {
            String sqlQuery = "SELECT review_id, content, is_positive, useful, user_id, film_id " +
                    "FROM Review WHERE review_id=?";
            return jdbcTemplate.queryForObject(sqlQuery, mapToReview(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Review not exist");
        }
    }

    @Override
    public List<Review> getReviewsByFilmIdAndCount(Integer filmId, Integer count) {
        if (Objects.isNull(filmId)) {
            String sqlQuery = "SELECT review_id, content, is_positive, useful, user_id, film_id " +
                    "FROM Review " +
                    "ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sqlQuery, mapToReview(), count);
        } else {
            String sqlQuery = "SELECT review_id, content, is_positive, useful, user_id, film_id " +
                    "FROM Review WHERE film_id=? " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sqlQuery, mapToReview(), filmId, count);
        }
    }

    @Override
    public void addLikeToReview(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO Useful (user_id, review_id, is_positive) VALUES (?, ?, TRUE)";
        jdbcTemplate.update(sqlQuery, userId, reviewId);
        String plusQuery = "UPDATE Review SET useful = useful + 1 WHERE review_id=?";
        jdbcTemplate.update(plusQuery, reviewId);
    }

    @Override
    public void addDislikeToReview(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO Useful (user_id, review_id, is_positive) VALUES (?, ?, FALSE)";
        jdbcTemplate.update(sqlQuery, userId, reviewId);
        String plusQuery = "UPDATE Review SET useful = useful - 1 WHERE review_id=?";
        jdbcTemplate.update(plusQuery, reviewId);
    }

    @Override
    public void removeLikeFromReview(Integer reviewId, Integer userId) {
        String sqlQuery = "DELETE FROM Useful WHERE user_id = ? AND review_id = ? AND is_positive = TRUE";
        jdbcTemplate.update(sqlQuery, userId, reviewId);
        String plusQuery = "UPDATE Review SET useful = useful - 1 WHERE review_id=?";
        jdbcTemplate.update(plusQuery, reviewId);
    }

    @Override
    public void removeDislikeFromReview(Integer reviewId, Integer userId) {
        String sqlQuery = "DELETE FROM Useful WHERE user_id = ? AND review_id = ? AND is_positive = FALSE";
        jdbcTemplate.update(sqlQuery, userId, reviewId);
        String plusQuery = "UPDATE Review SET useful = useful + 1 WHERE review_id=?";
        jdbcTemplate.update(plusQuery, reviewId);
    }

    private void forHandleReviewIdWithPostmanExceptionsFindUser(Integer id) {
        SqlRowSet rsUser = jdbcTemplate.queryForRowSet("SELECT user_id FROM User_Filmorate WHERE user_id=?", id);
        if (!rsUser.next()) {
            throw new EntityNotFoundException("User not exist");
        }
    }

    private void forHandleReviewIdWithPostmanExceptionsFindFilm(Integer id) {
        SqlRowSet rsFilm = jdbcTemplate.queryForRowSet("SELECT film_id FROM Film WHERE film_id=?", id);
        if (!rsFilm.next()) {
            throw new EntityNotFoundException("Film not exist");
        }
    }

    private void entityValidation(Review review) {
        if (review.getContent() == null) {
            throw new BadRequestException("Content must be not null");
        }
        if (review.getIsPositive() == null) {
            throw new BadRequestException("IsPositive field must be not null");
        }
        if (review.getUserId() == null) {
            throw new BadRequestException("User id must be not null");
        }
        if (review.getUserId() < 0) {
            throw new EntityNotFoundException("User not exist");
        }
        if (review.getFilmId() == null) {
            throw new BadRequestException("Film id must be not null");
        }
        if (review.getFilmId() < 0) {
            throw new EntityNotFoundException("Film not exist");
        }
    }
}