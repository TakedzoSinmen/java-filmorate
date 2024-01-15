package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Review;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReviewDaoStorageImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    private ReviewDaoStorageImpl reviewDaoStorage;
    private Review validReview;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewDaoStorage = new ReviewDaoStorageImpl(jdbcTemplate);
        validReview = Review.builder()
                .reviewId(1)
                .content("Great movie!")
                .isPositive(true)
                .userId(1)
                .filmId(1)
                .useful(0)
                .build();
    }

    @Test
    void testUpdateReviewWhenValidReviewThenReviewUpdated() {
        when(jdbcTemplate.update(anyString(), any(), any(), anyInt())).thenReturn(1);
        when(jdbcTemplate.queryForObject(
                anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt()
        )).thenReturn(validReview);

        Review result = reviewDaoStorage.updateReview(validReview);

        assertEquals(validReview, result);
        verify(jdbcTemplate).update(anyString(), any(), any(), anyInt());
    }

    @Test
    void testDeleteReviewWhenReviewDeletedThenReviewDeleted() {
        when(jdbcTemplate.update(anyString(), anyInt())).thenReturn(1);

        reviewDaoStorage.deleteReview(validReview.getReviewId());

        verify(jdbcTemplate).update(anyString(), anyInt());
    }

    @Test
    void testGetReviewByIdWhenReviewFoundThenReviewReturned() {
        when(jdbcTemplate.queryForObject(
                anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt()
        )).thenReturn(validReview);

        Review result = reviewDaoStorage.getReviewById(validReview.getReviewId()).getBody();

        assertEquals(validReview, result);
        verify(jdbcTemplate).queryForObject(anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt()
        );
    }

    @Test
    void testGetReviewByIdWhenReviewNotFoundThenEntityNotFoundException() {
        when(jdbcTemplate.queryForObject(anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt()
        )).thenThrow(new EmptyResultDataAccessException(1));

        Executable executable = () -> reviewDaoStorage.getReviewById(validReview.getReviewId());

        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void testGetReviewsByFilmIdAndCountWhenFilmIdNullThenReviewsReturned() {
        List<Review> expectedReviews = Collections.singletonList(validReview);
        when(jdbcTemplate.query(anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt()
        )).thenReturn(expectedReviews);

        List<Review> result = reviewDaoStorage.getReviewsByFilmIdAndCount(null, 1);

        assertEquals(expectedReviews, result);
        verify(jdbcTemplate).query(anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt()
        );
    }

    @Test
    void testGetReviewsByFilmIdAndCountWhenFilmIdNotNullThenReviewsReturned() {
        List<Review> expectedReviews = Collections.singletonList(validReview);
        when(jdbcTemplate.query(anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt(),
                anyInt()
        )).thenReturn(expectedReviews);

        List<Review> result = reviewDaoStorage.getReviewsByFilmIdAndCount(validReview.getFilmId(), 1);

        assertEquals(expectedReviews, result);
        verify(jdbcTemplate).query(anyString(),
                (RowMapper<Review>) any(RowMapper.class),
                anyInt(),
                anyInt()
        );
    }

    @Test
    void testAddLikeToReviewWhenLikeAddedThenLikeAdded() {
        // Заглушеки для обоих вызовов метода update, так как они у нас действительно дважды вызываются :)
        when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenReturn(1);
        when(jdbcTemplate.update(anyString(), anyInt())).thenReturn(1);

        reviewDaoStorage.addLikeToReview(validReview.getReviewId(), validReview.getUserId());

        // Проверка, первого update
        verify(jdbcTemplate).update(eq("INSERT INTO Useful (user_id, review_id, is_positive) VALUES (?, ?, TRUE)"),
                eq(validReview.getUserId()), eq(validReview.getReviewId()));
        // Проверка, второго
        verify(jdbcTemplate).update(eq("UPDATE Review SET useful = useful + 1 WHERE review_id=?"),
                eq(validReview.getReviewId()));
    }

    @Test
    void testAddDislikeToReviewWhenDislikeAddedThenDislikeAdded() {
        when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenReturn(1);
        when(jdbcTemplate.update(anyString(), anyInt())).thenReturn(1);

        reviewDaoStorage.addDislikeToReview(validReview.getReviewId(), validReview.getUserId());

        verify(jdbcTemplate).update(eq("INSERT INTO Useful (user_id, review_id, is_positive) VALUES (?, ?, FALSE)"),
                eq(validReview.getUserId()), eq(validReview.getReviewId()));
        verify(jdbcTemplate).update(eq("UPDATE Review SET useful = useful - 1 WHERE review_id=?"),
                eq(validReview.getReviewId()));
    }

    @Test
    void testRemoveLikeFromReviewWhenLikeRemovedThenLikeRemoved() {
        when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenReturn(1);
        when(jdbcTemplate.update(anyString(), anyInt())).thenReturn(1);

        reviewDaoStorage.removeLikeFromReview(validReview.getReviewId(), validReview.getUserId());

        verify(jdbcTemplate).update(eq("DELETE FROM Useful WHERE user_id = ? AND review_id = ? AND is_positive = TRUE"),
                eq(validReview.getUserId()), eq(validReview.getReviewId()));
        verify(jdbcTemplate).update(eq("UPDATE Review SET useful = useful - 1 WHERE review_id=?"),
                eq(validReview.getReviewId()));
    }

    @Test
    void testRemoveDislikeFromReviewWhenDislikeRemovedThenDislikeRemoved() {
        when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenReturn(1);

        reviewDaoStorage.removeDislikeFromReview(validReview.getReviewId(), validReview.getUserId());

        verify(jdbcTemplate).update(eq("DELETE FROM Useful WHERE user_id = ? AND review_id = ? AND is_positive = FALSE"),
                eq(validReview.getUserId()), eq(validReview.getReviewId()));
        verify(jdbcTemplate).update(eq("UPDATE Review SET useful = useful + 1 WHERE review_id=?"),
                eq(validReview.getReviewId()));
    }
}