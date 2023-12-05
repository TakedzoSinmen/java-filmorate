package ru.yandex.practicum.storage.api;

import ru.yandex.practicum.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(Integer id);
}
