package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.enums.EventType;
import ru.yandex.practicum.model.enums.Operation;
import ru.yandex.practicum.storage.api.EventStorage;
import ru.yandex.practicum.storage.api.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public void addFriend(Integer coreId, Integer friendId) {
        userStorage.addFriend(coreId, friendId);
        eventStorage.addEvent(Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(coreId)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(friendId)
                .build()
        );
    }

    public void removeFriend(Integer coreId, Integer friendId) {
        userStorage.removeFriend(coreId, friendId);
        eventStorage.addEvent(Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(coreId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .entityId(friendId)
                .build()
        );
    }

    public List<User> searchForUserFriends(Integer id) {
        return userStorage.searchForUserFriends(id);
    }

    public List<User> searchForSameFriends(Integer userId, Integer friendId) {
        return userStorage.searchForSameFriends(userId, friendId);
    }

    public User addUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        try {
            return userStorage.getUserById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " does not exist."));
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("User with id " + id + " does not exist.");
        }
    }

    public void deleteUserById(Integer id) {
        userStorage.deleteUserById(id);
    }

    public List<Film> recommendations(Integer id) {
        List<Film> films = userStorage.recommendations(id);
        userStorage.load(films);
        return films;
    }
}