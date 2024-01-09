package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.Operation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Integer eventId;
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private Operation operation;
    private Integer entityId;
}
