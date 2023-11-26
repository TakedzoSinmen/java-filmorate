package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    private int id = 1;

    private void generateId() {
        id++;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateBody(user);
        user.setId(id);
        users.put(id, user);
        generateId();
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
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

    @GetMapping
    public List<User> getUsers() {
        log.info("запрос GET/users");
        return new ArrayList<>(users.values());
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
