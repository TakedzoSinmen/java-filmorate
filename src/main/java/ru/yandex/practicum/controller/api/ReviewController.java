package ru.yandex.practicum.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Review;
import ru.yandex.practicum.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        log.debug("POST request received to add new review");
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        log.debug("PUT request received to update review");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable(value = "id") Integer id) {
        log.debug("DELETE request received to delete review by id = {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable(value = "id") Integer id) {
        log.debug("GET request received to get review by id = {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilmIdAndCount(@RequestParam(value = "filmId", required = false) Integer filmId,
                                                   @RequestParam(value = "count", defaultValue = "10")
                                                   @Positive Integer count)
            throws SQLException {
        return reviewService.getReviewsByFilmIdAndCount(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable(value = "id") Integer id,
                                @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT request received to add like on review");
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable(value = "id") Integer id,
                                   @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT request received to add dislike on review");
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable(value = "id") Integer id,
                                     @PathVariable(value = "userId") Integer userId) {
        log.debug("DELETE request received to delete like on review");
        reviewService.removeLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable(value = "id") Integer id,
                                        @PathVariable(value = "userId") Integer userId) {
        log.debug("DELETE request received to delete dislike on review");
        reviewService.removeDislikeFromReview(id, userId);
    }
}