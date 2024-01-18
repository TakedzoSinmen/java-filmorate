package ru.yandex.practicum.storage.daoImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.Operation;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.api.EventStorage;
import ru.yandex.practicum.storage.api.FilmStorage;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getEventFeed(Integer id) {
        isUserExist(id);
        String sql = "SELECT ef.event_id, ef.timestamp, ef.user_id, ef.event_type, ef.operation, ef.entity_id " +
                "FROM Event_Feed AS ef " +
                "WHERE ef.user_id = ?";
        return jdbcTemplate.query(sql, getEventFeedMapper(), id);
    }

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO Event_Feed (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, System.currentTimeMillis(), event.getUserId(), event.getEventType().toString(),
                event.getOperation().toString(), event.getEntityId());
    }

    private RowMapper<Event> getEventFeedMapper() {
        return (rs, rowNum) ->
                Event.builder()
                        .eventId(rs.getInt("event_id"))
                        .timestamp(rs.getLong("timestamp"))
                        .userId(rs.getInt("user_id"))
                        .eventType(EventType.valueOf(rs.getString("event_type").toUpperCase()))
                        .operation(Operation.valueOf(rs.getString("operation").toUpperCase()))
                        .entityId(rs.getInt("entity_id"))
                        .build();
    }

    private void isUserExist(Integer userId) {
        String sql = "SELECT user_id FROM User_Filmorate WHERE user_id = ?";
        if (!jdbcTemplate.queryForRowSet(sql, userId).next()) {
            log.warn("User with id = {} was not found", userId);
            throw new EntityNotFoundException(String.format("User with id = %d was not found", userId));
        }
    }

}