//package ru.yandex.practicum.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.function.Executable;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//import ru.yandex.practicum.exception.EntityNotFoundException;
//import ru.yandex.practicum.model.Event;
//import ru.yandex.practicum.model.Review;
//import ru.yandex.practicum.storage.api.ReviewStorage;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceTest {
//
//    @Mock
//    private ReviewStorage reviewStorage;
//    @Mock
//    private EventService eventService;
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    @Test
//    void whenAddReview_thenCreateEventAndReturnReview() {
//        Review review = new Review();
//        review.setUserId(1);
//        review.setReviewId(1);
//        when(reviewStorage.addReview(review)).thenReturn(review);
//
//        Review result = reviewService.addReview(review);
//
//        assertEquals(review, result);
//        verify(eventService).addEvent(any(Event.class));
//    }
//
//    @Test
//    void whenUpdateReview_thenCreateEventAndReturnUpdatedReview() {
//        Review review = new Review();
//        review.setUserId(1);
//        review.setReviewId(1);
//        when(reviewStorage.updateReview(review)).thenReturn(review);
//
//        Review result = reviewService.updateReview(review);
//
//        assertEquals(review, result);
//        verify(eventService).addEvent(any(Event.class));
//    }
//
//    @Test
//    void whenDeleteReview_thenCreateEvent() {
//        Integer reviewId = 1;
//        Review review = new Review();
//        review.setUserId(1);
//        review.setReviewId(reviewId);
//        when(reviewStorage.getReviewById(reviewId)).thenReturn(ResponseEntity.of(Optional.of(review)));
//        doNothing().when(reviewStorage).deleteReview(reviewId);
//
//        reviewService.deleteReview(reviewId);
//
//        verify(reviewStorage).deleteReview(reviewId);
//        verify(eventService).addEvent(any(Event.class));
//    }
//
//    @Test
//    void whenGetReviewById_thenReturnReview() {
//        Integer reviewId = 1;
//        Review expectedReview = new Review();
//        when(reviewStorage.getReviewById(reviewId)).thenReturn(ResponseEntity.of(Optional.of(expectedReview)));
//
//        Review actualReview = reviewService.getReviewById(reviewId);
//
//        assertEquals(expectedReview, actualReview);
//    }
//
//    @Test
//    void testGetReviewByIdWhenReviewDoesNotExistThenThrowEntityNotFoundException() {
//        Integer reviewId = 999;
//        when(reviewStorage.getReviewById(reviewId)).thenReturn(null);
//
//        Executable executable = () -> reviewService.getReviewById(reviewId);
//
//        assertThrows(NullPointerException.class, executable);
//    }
//
//    @Test
//    void whenGetReviewsByFilmIdAndCount_thenReturnListOfReviews() {
//        Integer filmId = 1;
//        Integer count = 10;
//        List<Review> expectedReviews = Arrays.asList(new Review(), new Review());
//        when(reviewStorage.getReviewsByFilmIdAndCount(filmId, count)).thenReturn(expectedReviews);
//
//        List<Review> actualReviews = reviewService.getReviewsByFilmIdAndCount(filmId, count);
//
//        assertEquals(expectedReviews, actualReviews);
//    }
//
//    @Test
//    void whenAddLikeToReview_thenNoException() {
//        Integer reviewId = 1;
//        Integer userId = 1;
//        doNothing().when(reviewStorage).addLikeToReview(reviewId, userId);
//
//        reviewService.addLikeToReview(reviewId, userId);
//
//        verify(reviewStorage).addLikeToReview(reviewId, userId);
//    }
//
//    @Test
//    void whenAddLikeToNonExistentReview_thenThrowEntityNotFoundException() {
//        Integer reviewId = 999;
//        Integer userId = 1;
//        doThrow(new EntityNotFoundException("Review not found")).when(reviewStorage).addLikeToReview(reviewId, userId);
//        Executable executable = () -> reviewService.addLikeToReview(reviewId, userId);
//
//        assertThrows(EntityNotFoundException.class, executable);
//    }
//
//    @Test
//    void whenAddDislikeToReview_thenNoException() {
//        Integer reviewId = 1;
//        Integer userId = 1;
//        doNothing().when(reviewStorage).addDislikeToReview(reviewId, userId);
//
//        reviewService.addDislikeToReview(reviewId, userId);
//
//        verify(reviewStorage).addDislikeToReview(reviewId, userId);
//    }
//
//    @Test
//    void whenAddDislikeToNonExistentReview_thenThrowEntityNotFoundException() {
//        Integer reviewId = 999;
//        Integer userId = 1;
//        doThrow(new EntityNotFoundException("Review not found")).when(reviewStorage).addDislikeToReview(reviewId, userId);
//        Executable executable = () -> reviewService.addDislikeToReview(reviewId, userId);
//
//        assertThrows(EntityNotFoundException.class, executable);
//    }
//
//    @Test
//    void whenRemoveLikeFromReview_thenNoException() {
//        Integer reviewId = 1;
//        Integer userId = 1;
//        doNothing().when(reviewStorage).removeLikeFromReview(reviewId, userId);
//
//        reviewService.removeLikeFromReview(reviewId, userId);
//
//        verify(reviewStorage).removeLikeFromReview(reviewId, userId);
//    }
//
//    @Test
//    void whenRemoveLikeFromNonExistentReview_thenThrowEntityNotFoundException() {
//        Integer reviewId = 999;
//        Integer userId = 1;
//        doThrow(new EntityNotFoundException("Review not found")).when(reviewStorage).removeLikeFromReview(reviewId, userId);
//        Executable executable = () -> reviewService.removeLikeFromReview(reviewId, userId);
//
//        assertThrows(EntityNotFoundException.class, executable);
//    }
//
//    @Test
//    void whenRemoveDislikeFromReview_thenNoException() {
//        Integer reviewId = 1;
//        Integer userId = 1;
//        doNothing().when(reviewStorage).removeDislikeFromReview(reviewId, userId);
//
//        reviewService.removeDislikeFromReview(reviewId, userId);
//
//        verify(reviewStorage).removeDislikeFromReview(reviewId, userId);
//    }
//
//    @Test
//    void whenRemoveDislikeFromNonExistentReview_thenThrowEntityNotFoundException() {
//        Integer reviewId = 999;
//        Integer userId = 1;
//        doThrow(new EntityNotFoundException("Review not found")).when(reviewStorage).removeDislikeFromReview(reviewId, userId);
//        Executable executable = () -> reviewService.removeDislikeFromReview(reviewId, userId);
//
//        assertThrows(EntityNotFoundException.class, executable);
//    }
//}