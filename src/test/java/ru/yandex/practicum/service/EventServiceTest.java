package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.storage.api.EventStorage;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventStorage eventStorage;

    @InjectMocks
    private EventService eventService;

    @Test
    void testGetEventFeedWhenStorageReturnsEventsThenReturnEvents() {
        Integer userId = 1;
        List<Event> expectedEvents = Arrays.asList(
                new Event(1, 123456789L, userId, null, null, 10),
                new Event(2, 123456790L, userId, null, null, 20)
        );
        when(eventStorage.getEventFeed(userId)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.getEventFeed(userId);

        assertEquals(expectedEvents, actualEvents);
    }
}