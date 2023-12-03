package ru.yandex.practicum.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.impl.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Data
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Integer coreId, Integer friendId) {
        User coreUser = userStorage.getUserById(coreId);
        User friendUser = userStorage.getUserById(friendId);
        if (coreUser == null) {
            throw new EntityNotFoundException("Пользователь с id: " + coreId + " не найден");
        }
        if (friendUser == null) {
            throw new EntityNotFoundException("Пользователь с id: " + friendId + " не найден");
        }
        coreUser.addFriend(friendId);
        log.info("Пользователь с id: " + friendId + " добавлен в друзья " + coreUser);
        friendUser.addFriend(coreId);
        log.info("Пользователь с id: " + coreId + " добавлен в друзья " + friendUser);
        return coreUser;
    }

    public User removeFriend(Integer coreId, Integer friendId) {
        User coreUser = userStorage.getUserById(coreId);
        User friendUser = userStorage.getUserById(friendId);
        if (coreUser == null) {
            throw new EntityNotFoundException("Пользователь с id: " + coreId + " не найден");
        }
        if (friendUser == null) {
            throw new EntityNotFoundException("Пользователь с id: " + friendId + " не найден");
        }
        coreUser.removeFriend(friendId);
        log.info("Пользователь с id: " + friendId + " удален из списка друзей " + coreUser);
        friendUser.removeFriend(coreId);
        log.info("Пользователь с id: " + coreId + " удален из списка друзей " + friendUser);
        return coreUser;
    }

    public List<Integer> searchForUserFriends(Integer id) {
        return userStorage.searchForUserFriends(id);
    }

    public List<Integer> searchForSameFriends(Integer id, Integer otherId) {
        List<Integer> result = new ArrayList<>();
        for (Integer searchForUserFriend : searchForUserFriends(id)) {
            for (Integer forUserFriend : searchForUserFriends(otherId)) {
                if (Objects.equals(searchForUserFriend, forUserFriend)) {
                    result.add(searchForUserFriend);
                }
            }
        }
        return result;
    }
}


