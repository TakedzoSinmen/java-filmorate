package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.Review;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.Operation;
import ru.yandex.practicum.storage.api.EventStorage;
import ru.yandex.practicum.storage.api.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    public Review addReview(Review review) {
        Review createdReview = reviewStorage.addReview(review);
        eventStorage.addEvent(Event.builder()
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(review.getReviewId())
                .build()
        );
        return createdReview;
    }

    public Review updateReview(Review review) {
        Review updatedReview = reviewStorage.updateReview(review);
        eventStorage.addEvent(Event.builder()
                .userId(review.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(review.getReviewId())
                .build()
        );
        return updatedReview;
    }

    public void deleteReview(Integer id) {
        Review review = getReviewById(id);
        reviewStorage.deleteReview(id);
        eventStorage.addEvent(Event.builder()
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .entityId(id)
                .build()
        );
    }

    public Review getReviewById(Integer id) {
        return reviewStorage.getReviewById(id).getBody();
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