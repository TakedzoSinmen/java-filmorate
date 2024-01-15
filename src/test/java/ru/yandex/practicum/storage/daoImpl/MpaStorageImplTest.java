package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Mpa;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MpaStorageImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private MpaStorageImpl mpaStorage;

    private Mpa expectedMpa;
    private List<Mpa> expectedMpaList;

    @BeforeEach
    void setUp() {
        expectedMpa = new Mpa(1, "PG-13");
        expectedMpaList = Arrays.asList(expectedMpa, new Mpa(2, "R"));
        reset(jdbcTemplate);
    }

    @Test
    void testGetMpaByIdWhenIdExistsThenReturnMpa() {
        when(jdbcTemplate.queryForObject(eq("SELECT mpa_id, mpa_name FROM Mpa WHERE mpa_id=?"),
                any(RowMapper.class), eq(1))).thenReturn(expectedMpa);
        Optional<Mpa> result = mpaStorage.getMpaById(1);
        assertTrue(result.isPresent(), "Mpa должен быть найден");
        assertEquals(expectedMpa, result.get(), "Возвращенный Mpa не соответствует ожидаемому");
    }

    @Test
    void testGetMpaByIdWhenIdDoesNotExistThenReturnEmpty() {
        when(jdbcTemplate.queryForObject(eq("SELECT mpa_id, mpa_name FROM Mpa WHERE mpa_id=?"),
                any(RowMapper.class), eq(999))).thenReturn(null);
        Optional<Mpa> result = mpaStorage.getMpaById(999);
        assertFalse(result.isPresent(), "Mpa не должен быть найден");
    }

    @Test
    void testGetAllMpaWhenMpasExistThenReturnMpas() {
        when(jdbcTemplate.query(eq("SELECT mpa_id, mpa_name FROM Mpa"), any(RowMapper.class)))
                .thenReturn(expectedMpaList);
        List<Mpa> result = mpaStorage.getAllMpa();
        assertNotNull(result, "Список Mpa не должен быть null");
        assertEquals(expectedMpaList.size(), result.size(), "Размер возвращаемого списка Mpa не соответствует ожидаемому");
        assertIterableEquals(expectedMpaList, result, "Возвращенный список Mpa не соответствует ожидаемому");
    }

    @Test
    void testGetAllMpaWhenNoMpasExistThenReturnEmptyList() {
        when(jdbcTemplate.query(eq("SELECT mpa_id, mpa_name FROM Mpa"), any(RowMapper.class)))
                .thenReturn(Arrays.asList());
        List<Mpa> result = mpaStorage.getAllMpa();
        assertTrue(result.isEmpty(), "Список Mpa должен быть пустым");
    }
}