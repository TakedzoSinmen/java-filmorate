package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.exception.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeStorageImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LikeStorageImpl likeStorage;

    @BeforeEach
    void setUp() {
        reset(jdbcTemplate);
    }

    @Test
    void testLikeWhenLikeAddedThenSuccess() {
        Integer filmId = 1;
        Integer userId = 1;
        when(jdbcTemplate.update(anyString(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(1);

        likeStorage.like(filmId, userId);

        verify(jdbcTemplate).update(anyString(), eq(filmId), eq(userId), eq(filmId), eq(userId));
    }

    @Test
    void testUnLikeWhenLikeRemovedThenSuccess() {
        Integer filmId = 1;
        Integer userId = 1;
        when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenReturn(1);

        ResponseEntity<String> response = likeStorage.unLike(filmId, userId);

        assertEquals("Like removed successfully", response.getBody());
        verify(jdbcTemplate).update(anyString(), eq(filmId), eq(userId));
    }

    @Test
    void testUnLikeWhenInvalidFilmIdThenException() {
        Integer filmId = -1;
        Integer userId = 1;

        Executable executable = () -> likeStorage.unLike(filmId, userId);

        assertThrows(EntityNotFoundException.class, executable);
        verify(jdbcTemplate, never()).update(anyString(), anyInt(), anyInt());
    }

    @Test
    void testUnLikeWhenInvalidUserIdThenException() {
        Integer filmId = 1;
        Integer userId = -1;

        Executable executable = () -> likeStorage.unLike(filmId, userId);

        assertThrows(EntityNotFoundException.class, executable);
        verify(jdbcTemplate, never()).update(anyString(), anyInt(), anyInt());
    }

    @Test
    void testGetLikesByFilmIdWhenLikesExistThenSuccess() {
        Integer filmId = 1;
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.next()).thenReturn(true, true, false);
        when(sqlRowSet.getInt("user_id")).thenReturn(10, 20);
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(sqlRowSet);

        List<Integer> likes = likeStorage.getLikesByFilmId(filmId);

        assertEquals(List.of(10, 20), likes);
        verify(jdbcTemplate).queryForRowSet(anyString(), eq(filmId));
    }

    @Test
    void testGetLikesByFilmIdWhenNoLikesThenEmptyList() {
        Integer filmId = 1;
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.next()).thenReturn(false);
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(sqlRowSet);

        List<Integer> likes = likeStorage.getLikesByFilmId(filmId);

        assertTrue(likes.isEmpty());
        verify(jdbcTemplate).queryForRowSet(anyString(), eq(filmId));
    }
}