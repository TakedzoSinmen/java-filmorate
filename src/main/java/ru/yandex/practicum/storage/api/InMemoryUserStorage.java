package ru.yandex.practicum.storage.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.impl.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        log.info("запрос PUT/users");
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
        log.info("запрос GET/users");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователя с id: " + id + " не найдено");
        }
        return users.get(id);
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

    @Override
    public List<Integer> searchForUserFriends (Integer id) {
        User user = users.get(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователя с id: " + id + " не найдено");
        }
        List<Integer> result = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            if (users.containsKey(friendId)) {
                result.add(friendId);
            }
        }
        return result;
    }

}
