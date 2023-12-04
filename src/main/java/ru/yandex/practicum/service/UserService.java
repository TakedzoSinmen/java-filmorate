package ru.yandex.practicum.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.api.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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

    public List<User> searchForUserFriends(Integer id) {
        User user = userStorage.getUserById(id);
        if (user.getFriends().isEmpty()) {
            throw new EntityNotFoundException("у пользователя с id: " + id + " список друзей пуст");
        }
        List<User> userFriends = new ArrayList<>();
        for (Integer friend : user.getFriends()) {
            userFriends.add(userStorage.getUserById(friend));
        }
        return userFriends;
    }

    public List<User> searchForSameFriends(Integer id, Integer otherId) {
        List<User> result = new ArrayList<>();
        User coreUser = userStorage.getUserById(id);
        if (coreUser == null) {
            throw new EntityNotFoundException("пользователя с id: " + id + " не найдено");
        }
        User otherUser = userStorage.getUserById(otherId);
        if (otherUser == null) {
            throw new EntityNotFoundException("пользователя с id: " + otherId + " не найдено");
        }
        List<Integer> coreUserFriendsId = new ArrayList<>(coreUser.getFriends());
        if (coreUserFriendsId.isEmpty()) {
            return Collections.emptyList();
        }
        for (Integer integer : coreUserFriendsId) {
            for (Integer friend : otherUser.getFriends()) {
                if (Objects.equals(integer, friend)) {
                    result.add(userStorage.getUserById(integer));
                }
            }
        }
        return result;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }
}


