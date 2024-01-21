package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.Operation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class EventDBStorageTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private EventDBStorage eventDBStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reset(jdbcTemplate);
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