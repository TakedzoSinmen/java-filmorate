package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.api.FilmStorage;
import ru.yandex.practicum.storage.api.UserStorage;
import ru.yandex.practicum.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

//    private UserStorage userStorage;
//    private UserService userService;
//    private FilmStorage filmStorage;
//    private FilmService filmService;
//    private User user;
//    private User user2;
//    private User wrongUser;
//
//    @BeforeEach
//    void setUp() {
//        user = new User(1, "mail@mail.com", "kobra", "Stas",
//                LocalDate.of(1983, 10, 3), null);
//        user2 = new User(2, "m@m.ru", "shlang", "Vitalik",
//                LocalDate.of(1988, 6, 13), null);
//        wrongUser = new User(999, "", "", "", LocalDate.now(), null);
//        userStorage = new InMemoryUserStorage();
//        userService = new UserService(userStorage);
//        filmStorage = new InMemoryFilmStorage();
//        filmService = new FilmService(filmStorage, userStorage);
//    }
//
//    @Test
//    void givenUserEntity_whenSaveEntityToMap_thenMapSizeGrowAndGotEntity() {
//        userService.addUser(user);
//
//        assertEquals(1, userService.getUsers().size());
//
//        userService.addUser(user2);
//
//        assertEquals(2, userService.getUsers().size());
//
//        User newUser = userService.getUserById(user.getId());
//
//        assertEquals(newUser, userService.getUsers().get(0));
//    }
//
//    @Test
//    void givenWrongUser_whenTryToAddUser_thenThrowCustomValidationException() {
//        Executable executable = () -> userService.addUser(wrongUser);
//
//        assertThrows(CustomValidationException.class, executable);
//    }
//
//    @Test
//    void givenNewEntityWithRightId_whenUseUpdateUserMethod_thenEntityMustBeReplacedWithNewGivenOne() {
//        userService.addUser(user);
//
//        assertEquals(user.getEmail(), userService.getUsers().get(0).getEmail());
//
//        User newUser = new User(1, "fail@fail.ccc", "neKobra", "neStas",
//                LocalDate.of(1995, 1, 12), null);
//        userService.updateUser(newUser);
//
//        assertEquals(newUser.getEmail(), userService.getUsers().get(0).getEmail());
//    }
//
//    @Test
//    void givenRightId_whenAddFriend_thenFriendsListFilledIn() {
//        userService.addUser(user);
//        userService.addUser(user2);
//        userService.addFriend(1, 2);
//
//        assertEquals(1, user.getFriends().size());
//        assertEquals(1, user2.getFriends().size());
//    }
//
//    @Test
//    void givenFriendId_whenRemoveFriendFromFriendList_thenFriendListMustBeClear() {
//        userService.addUser(user);
//        userService.addUser(user2);
//        userService.addFriend(user.getId(), user2.getId());
//
//        assertEquals(1, user.getFriends().size());
//        assertEquals(1, user2.getFriends().size());
//
//        userService.removeFriend(user.getId(), user2.getId());
//
//        assertTrue(user.getFriends().isEmpty());
//    }
}