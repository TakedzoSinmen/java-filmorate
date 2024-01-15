package ru.yandex.practicum.controller.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.service.RecommendationService;
import ru.yandex.practicum.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final RecommendationService recommendationService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("POST request received to create new entity User");
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("PUT request received to update entity");
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("GET request received to receive all users");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.debug("GET request received to receive user by given id= {}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id") Integer id, @PathVariable(value = "friendId") Integer friendId) {
        log.debug("PUT request received to add friendly relations by given user id= {} and friend id= {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(value = "id") Integer id, @PathVariable(value = "friendId") Integer friendId) {
        log.debug("DELETE request received to remove entity from friend list by given core user " +
                "id= {} and friend id= {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> searchForUserFriends(@PathVariable Integer id) {
        log.debug("GET request received to receive user friend list by given user id= {} ", id);
        return userService.searchForUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> searchForSameFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.debug("GET request received to search for common friends if they exist");
        return userService.searchForSameFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable(value = "id") Integer id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEventFeed(@PathVariable(value = "id") Integer id) {
        log.debug("GET request received to receive event feed of user with id = {}", id);
        return eventService.getEventFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> recommendations(@PathVariable(value = "id") Integer id) {
        log.debug("GET request received to receive recommendations to user with id= {}", id);
        return recommendationService.recommendations(id);
    }
}