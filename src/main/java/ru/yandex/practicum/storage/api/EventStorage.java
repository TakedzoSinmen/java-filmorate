package ru.yandex.practicum.storage.api;

import ru.yandex.practicum.model.Event;

import java.util.List;

public interface EventStorage {

    List<Event> getEventFeed(Integer id);

    void addEvent(Event event);
}
