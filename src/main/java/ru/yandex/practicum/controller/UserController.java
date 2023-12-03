package ru.yandex.practicum.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.impl.UserStorage;

import java.util.List;

@Data
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userStorage.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable(value = "id") Integer id, @PathVariable(value = "friendId") Integer friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable(value = "id") Integer id, @PathVariable(value = "friendId") Integer friendId) {
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> searchForUserFriends(@PathVariable Integer id) {
        return userService.searchForUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> searchForSameFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.searchForSameFriends(id, otherId);
    }
}
