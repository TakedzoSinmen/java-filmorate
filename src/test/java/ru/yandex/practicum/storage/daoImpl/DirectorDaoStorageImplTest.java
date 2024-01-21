package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectorDaoStorageImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private SqlRowSet rowSet;

    @InjectMocks
    private DirectorDaoStorageImpl directorDaoStorage;

    private Director director;

    @BeforeEach
    void setUp() {
        director = Director.builder().id(1).name("John Doe").build();
        reset(jdbcTemplate);
    }

    @Test
    void testGetByIdWhenDirectorExistsThenReturnDirector() {
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(true);
        when(rowSet.getInt("director_id")).thenReturn(director.getId());
        when(rowSet.getString("director_name")).thenReturn(director.getName());

        Director result = directorDaoStorage.getById(director.getId());

        assertNotNull(result);
        assertEquals(director.getId(), result.getId());
        assertEquals(director.getName(), result.getName());
    }

    @Test
    void testGetByIdWhenDirectorNotExistsThenThrowEntityNotFoundException() {
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(false);

        Executable executable = () -> directorDaoStorage.getById(director.getId());

        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void testGetAllThenReturnAllDirectors() {
        when(jdbcTemplate.queryForRowSet(anyString())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(true, true, false);
        when(rowSet.getInt("director_id")).thenReturn(director.getId(), director.getId() + 1);
        when(rowSet.getString("director_name")).thenReturn(director.getName(), "Jane Doe");

        List<Director> result = directorDaoStorage.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateByIdWhenDirectorExistsThenUpdateDirector() {
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(true);
        when(jdbcTemplate.update(eq("UPDATE Director SET director_name = ? WHERE director_id =?"),
                eq("Jane Doe"), eq(1))).thenReturn(1);

        Director updatedDirector = Director.builder().id(director.getId()).name("Jane Doe").build();
        Director result = directorDaoStorage.updateById(updatedDirector);

        assertNotNull(result);
        assertEquals(updatedDirector.getName(), result.getName());
    }

    @Test
    void testUpdateByIdWhenDirectorNotExistsThenThrowEntityNotFoundException() {
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(false);

        Director updatedDirector = Director.builder().id(director.getId()).name("Jane Doe").build();
        Executable executable = () -> directorDaoStorage.updateById(updatedDirector);

        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void testDeleteByIdWhenDirectorExistsThenDeleteDirector() {
        int directorId = director.getId();
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(true);
        when(jdbcTemplate.update(eq("DELETE FROM Director WHERE director_id =?"), eq(directorId))).thenReturn(1);

        assertDoesNotThrow(() -> directorDaoStorage.deleteById(directorId));
        verify(jdbcTemplate, times(1)).update(eq("DELETE FROM Director WHERE director_id =?"), eq(directorId));
    }

    @Test
    void testDeleteByIdWhenDirectorNotExistsThenThrowEntityNotFoundException() {
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(false);

        Executable executable = () -> directorDaoStorage.deleteById(director.getId());

        assertThrows(EntityNotFoundException.class, executable);
    }
}