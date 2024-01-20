//package ru.yandex.practicum.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.function.Executable;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//
//import ru.yandex.practicum.exception.EntityNotFoundException;
//import ru.yandex.practicum.model.Film;
//import ru.yandex.practicum.storage.api.GenreStorage;
//import ru.yandex.practicum.storage.api.LikeStorage;
//import ru.yandex.practicum.storage.api.MpaStorage;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class RecommendationServiceTest {
//
//    @Mock
//    private JdbcTemplate jdbcTemplate;
//    @Mock
//    private LikeStorage likeStorage;
//    @Mock
//    private MpaStorage mpaStorage;
//    @Mock
//    private GenreStorage genreStorage;
//    @Mock
//    private DirectorService directorService;
//
//    @InjectMocks
//    private RecommendationService recommendationService;
//
//    @Test
//    @DisplayName("Получение списка рекомендаций по корректному ID пользователя")
//    void testRecommendationsWhenUserIdIsCorrectAndThereAreRecommendationsThenReturnListOfFilms() {
//        int userId = 1;
//        List<Film> expectedFilms = Collections.singletonList(new Film());
//        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId), eq(userId)))
//                .thenReturn(expectedFilms);
//
//        List<Film> actualFilms = recommendationService.recommendations(userId);
//
//        assertNotNull(actualFilms, "Список фильмов не должен быть null");
//        assertFalse(actualFilms.isEmpty(), "Список фильмов не должен быть пустым");
//        assertEquals(expectedFilms, actualFilms, "Список фильмов не соответствует ожидаемому");
//    }
//
//    @Test
//    @DisplayName("Выброс исключения при некорректном ID пользователя")
//    void testRecommendationsWhenUserIdIsIncorrectAndThereAreNoRecommendationsThenThrowEntityNotFoundException() {
//        int userId = -1;
//        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId), eq(userId)))
//                .thenThrow(new RuntimeException());
//
//        Executable executable = () -> recommendationService.recommendations(userId);
//
//        assertThrows(EntityNotFoundException.class, executable, "Должно быть выброшено исключение EntityNotFoundException");
//    }
//
//    @Test
//    @DisplayName("Получение пустого списка рекомендаций по корректному ID пользователя")
//    void testRecommendationsWhenUserIdIsCorrectButThereAreNoRecommendationsThenReturnEmptyList() {
//        int userId = 1;
//        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId), eq(userId)))
//                .thenReturn(Collections.emptyList());
//
//        List<Film> actualFilms = recommendationService.recommendations(userId);
//
//        assertNotNull(actualFilms, "Список фильмов не должен быть null");
//        assertTrue(actualFilms.isEmpty(), "Список фильмов должен быть пустым");
//    }
//}