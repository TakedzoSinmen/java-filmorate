package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.Operation;
import ru.yandex.practicum.storage.api.FilmStorage;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventDBStorageTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private FilmStorage filmStorage;
    @InjectMocks
    private EventDBStorage eventDBStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reset(jdbcTemplate);
    }

    @Test
    void testGetEventFeedWhenUserExistsThenReturnListOfEvents() {
        Integer userId = 1;
        List<Event> expectedEvents = Arrays.asList(
                new Event(1, 123456789L, userId, EventType.LIKE, Operation.ADD, 10),
                new Event(2, 123456790L, userId, EventType.REVIEW, Operation.UPDATE, 20)
        );

        doNothing().when(filmStorage).isUserExist(userId);
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(userId)
        )).thenReturn(expectedEvents);

        List<Event> actualEvents = eventDBStorage.getEventFeed(userId);

        verify(filmStorage).isUserExist(userId);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(userId));
        assertEquals(expectedEvents, actualEvents, "Список событий не соответствует ожидаемому.");
    }

    @Test
    void testGetEventFeedWhenUserDoesNotExistThenThrowException() {
        Integer userId = 2;
        doThrow(new EntityNotFoundException("Пользователь не существует")).when(filmStorage).isUserExist(userId);

        Executable executable = () -> eventDBStorage.getEventFeed(userId);

        assertThrows(EntityNotFoundException.class, executable, "Должно быть выброшено исключение, если пользователь не существует.");
    }

    @Test
    void testAddEventWhenEventAddedSuccessfullyThenNoException() {
        Event event = new Event(null, null, 3, EventType.FRIEND, Operation.REMOVE, 30);
        when(jdbcTemplate.update(
                anyString(),
                anyLong(),
                eq(event.getUserId()),
                eq(event.getEventType().toString()),
                eq(event.getOperation().toString()),
                eq(event.getEntityId())
        )).thenReturn(1);

        assertDoesNotThrow(() -> eventDBStorage.addEvent(event), "Не должно быть исключения при успешном добавлении события.");

        verify(jdbcTemplate).update(
                anyString(),
                anyLong(),
                eq(event.getUserId()),
                eq(event.getEventType().toString()),
                eq(event.getOperation().toString()),
                eq(event.getEntityId())
        );
    }
}