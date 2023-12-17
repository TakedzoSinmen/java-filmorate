package ru.yandex.practicum.storage.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.api.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Data
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    private void generateId() {
        id++;
    }

    @Override
    public User addUser(User user) {
        validateBody(user);
        user.setId(id);
        users.put(id, user);
        generateId();
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateBody(user);
        log.debug("запрос PUT/users");
        User newUser = users.get(user.getId());
        if (newUser != null) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new CustomValidationException("пользователь не найден");
        }
    }

    @Override
    public List<User> getUsers() {
        log.debug("запрос GET/users");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователя с id: " + id + " не найдено");
        }
        Optional<User> user = Optional.of(users.get(id));
        if (user.isPresent()) {
            throw new EntityNotFoundException("User not exist");
        }
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
    }

    @Override
    public List<User> searchForUserFriends(int id) {
        return getUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not exist"))
                .getFriends();
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        try {
            User user = getUserById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not exist" + userId));
            User friend = getUserById(friendId)
                    .orElseThrow(() -> new EntityNotFoundException("User not exist" + friendId));
            List<User> userFriends = user.getFriends();
            List<User> friendFriends = friend.getFriends();
            if (userFriends != null) {
                userFriends.removeIf(u -> u.getId() == friendId);
            }
            if (friendFriends != null) {
                friendFriends.removeIf(u -> u.getId() == userId);
            }
        } catch (RuntimeException e) {
            throw new EntityNotFoundException("User not exist");
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not exist" + userId));
        User friend = getUserById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("User not exist" + friendId));
        List<User> userFriends = user.getFriends();
        List<User> friendFriends = friend.getFriends();
        if (userFriends != null) {
            userFriends.add(getUserById(friendId).get());
        }
        if (friendFriends != null) {
            friendFriends.add(getUserById(userId).get());
        }
    }

    @Override
    public List<User> searchForSameFriends(int userId, int friendId) {
        User user = getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not exist" + userId));
        User friend = getUserById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("User not exist" + friendId));
        if (user != null && friend != null) {
            List<User> userFriends = user.getFriends();
            List<User> friendFriends = friend.getFriends();
            if (userFriends != null && friendFriends != null) {
                Set<User> commonFriends = new HashSet<>(userFriends);
                commonFriends.retainAll(friendFriends);
                commonFriends.removeIf(u -> u.getId() == userId || u.getId() == friendId);
                return new ArrayList<>(commonFriends);
            } else {
                throw new EntityNotFoundException("User's or friend's friend list is null");
            }
        } else {
            throw new EntityNotFoundException("User or friend does not exist");
        }
    }

    private static void validateBody(User user) throws CustomValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new CustomValidationException("Некорректный адрес электронной почты");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new CustomValidationException("Некорректный логин");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new CustomValidationException("Дата рождения не может быть в будущем");
        }
    }
}