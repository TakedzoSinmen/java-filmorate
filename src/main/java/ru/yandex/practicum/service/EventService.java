package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.storage.api.EventStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public List<Event> getEventFeed(Integer id) {
        return eventStorage.getEventFeed(id);
    }
}
