package ru.yandex.practicum.storage.api;

import ru.yandex.practicum.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    Optional<User> getUserById(Integer id);

    void deleteUserById(Integer id);

    List<User> searchForUserFriends(int id);

    void removeFriend(int userId, int friendId);

    void addFriend(int userId, int friendId);

    List<User> searchForSameFriends(int userId, int idFriend);
}