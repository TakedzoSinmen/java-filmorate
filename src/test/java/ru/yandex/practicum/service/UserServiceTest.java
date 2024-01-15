package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Event;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.api.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @Mock
    private EventService eventService;

    @InjectMocks
    private UserService userService;

    @Test
    void testAddFriendWhenFriendAddedThenSuccess() {
        Integer coreId = 1;
        Integer friendId = 2;

        userService.addFriend(coreId, friendId);

        verify(userStorage).addFriend(coreId, friendId);
        verify(eventService).addEvent(any(Event.class));
    }

    @Test
    void testRemoveFriendWhenFriendRemovedThenSuccess() {
        Integer coreId = 1;
        Integer friendId = 2;

        userService.removeFriend(coreId, friendId);

        verify(userStorage).removeFriend(coreId, friendId);
        verify(eventService).addEvent(any(Event.class));
    }

    @Test
    void testSearchForUserFriendsWhenFriendsExistThenReturnFriends() {
        Integer userId = 1;
        List<User> expectedFriends = List.of(new User());
        when(userStorage.searchForUserFriends(userId)).thenReturn(expectedFriends);

        List<User> actualFriends = userService.searchForUserFriends(userId);

        assertEquals(expectedFriends, actualFriends);
    }

    @Test
    void testSearchForSameFriendsWhenSameFriendsExistThenReturnSameFriends() {
        Integer userId = 1;
        Integer friendId = 2;
        List<User> expectedSameFriends = List.of(new User());
        when(userStorage.searchForSameFriends(userId, friendId)).thenReturn(expectedSameFriends);

        List<User> actualSameFriends = userService.searchForSameFriends(userId, friendId);

        assertEquals(expectedSameFriends, actualSameFriends);
    }

    @Test
    void testAddUserWhenUserAddedThenReturnAddedUser() {
        User user = new User();
        user.setName("Test Name");
        user.setLogin("TestLogin");
        when(userStorage.addUser(user)).thenReturn(user);

        User addedUser = userService.addUser(user);

        assertEquals(user, addedUser);
    }

    @Test
    void testUpdateUserWhenUserUpdatedThenReturnUpdatedUser() {
        User user = new User();
        when(userStorage.updateUser(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user);

        assertEquals(user, updatedUser);
    }

    @Test
    void testGetUsersWhenUsersExistThenReturnUsers() {
        List<User> expectedUsers = List.of(new User());
        when(userStorage.getUsers()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void testGetUserByIdWhenUserExistsThenReturnUser() {
        int userId = 1;
        User expectedUser = new User();
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void testGetUserByIdWhenUserDoesNotExistThenThrowException() {
        int userId = 1;
        when(userStorage.getUserById(userId)).thenReturn(Optional.empty());

        Executable executable = () -> userService.getUserById(userId);

        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void testDeleteUserByIdWhenUserExistsThenSuccess() {
        Integer userId = 1;

        userService.deleteUserById(userId);

        verify(userStorage).deleteUserById(userId);
    }
}