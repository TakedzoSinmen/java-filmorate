package ru.yandex.practicum.storage.api;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Integer id);

    ResponseEntity<Review> getReviewById(Integer id);

    List<Review> getReviewsByFilmIdAndCount(Integer filmId, Integer count);

    void addLikeToReview(Integer id, Integer userId);

    void addDislikeToReview(Integer id, Integer userId);

    void removeLikeFromReview(Integer id, Integer userId);

    void removeDislikeFromReview(Integer id, Integer userId);

    void forHandleReviewIdWithPostmanExceptionsFindFilm(Integer id);

    void forHandleReviewIdWithPostmanExceptionsFindUser(Integer id);
}