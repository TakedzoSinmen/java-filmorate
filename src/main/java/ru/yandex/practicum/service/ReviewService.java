package ru.yandex.practicum.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Review;
import ru.yandex.practicum.storage.api.ReviewStorage;

import java.util.List;

@Data
@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Integer id) {
        reviewStorage.deleteReview(id);
    }

    public Review getReviewById(Integer id) {
        return reviewStorage.getReviewById(id);
    }

    public List<Review> getReviewsByFilmIdAndCount(Integer filmId, Integer count) {
        return reviewStorage.getReviewsByFilmIdAndCount(filmId, count);
    }

    public void addLikeToReview(Integer id, Integer userId) {
        reviewStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(Integer id, Integer userId) {
        reviewStorage.addDislikeToReview(id, userId);
    }

    public void removeLikeFromReview(Integer id, Integer userId) {
        reviewStorage.removeLikeFromReview(id, userId);
    }

    public void removeDislikeFromReview(Integer id, Integer userId) {
        reviewStorage.removeDislikeFromReview(id, userId);
    }
}
