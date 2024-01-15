package ru.yandex.practicum.storage.daoImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.api.UserStorage;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@JdbcTest
class UserDaoStorageImplTest {

    private UserStorage userDaoStorage;
    private User user;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        userDaoStorage = new UserDaoStorageImpl(jdbcTemplate);
        user = new User(1, "test@example.com", "testLogin", "Test User",
                LocalDate.of(1990, 1, 1), Collections.emptyList());
    }

    @Test
    void testGetUsersWhenCalledThenReturnListOfUsers() {
        List<User> users = userDaoStorage.getUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void testAddUserWhenCalledThenReturnUserWithId() {
        User newUser = userDaoStorage.addUser(user);

        assertThat(newUser.getId()).isEqualTo(1);
    }

    @Test
    void testGetUserByIdWhenCalledThenReturnOptionalWithUser() {
        userDaoStorage.addUser(user);

        Optional<User> resultUser = userDaoStorage.getUserById(user.getId());

        assertTrue(resultUser.isPresent());
        assertEquals(user, resultUser.get());
    }

    @Test
    void testUpdateUserWhenCalledThenReturnUpdatedUser() {
        userDaoStorage.addUser(user);

        User updatedUser = new User(user.getId(), "updated@example.com", "updatedLogin", "Updated User",
                LocalDate.of(1995, 1, 1), Collections.emptyList());
        User resultUser = userDaoStorage.updateUser(updatedUser);

        assertNotNull(resultUser);
        assertEquals(updatedUser, resultUser);
    }

    @Test
    void testDeleteUserByIdWhenCalledThenUserIsDeleted() {
        userDaoStorage.addUser(user);

        userDaoStorage.deleteUserById(user.getId());

        List<User> users = userDaoStorage.getUsers();
        assertEquals(0, users.size());
    }

    @Test
    void testSearchForUserFriendsWhenCalledThenReturnListOfFriends() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        userDaoStorage = new UserDaoStorageImpl(jdbcTemplate);

        when(jdbcTemplate.queryForObject(anyString(), any(Class.class), anyInt())).thenReturn(1);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenReturn(Arrays.asList(user));

        List<User> friends = userDaoStorage.searchForUserFriends(user.getId());

        assertNotNull(friends);
        assertEquals(1, friends.size());
    }

    @Test
    void testRemoveFriendWhenCalledThenFriendIsRemoved() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        userDaoStorage = new UserDaoStorageImpl(jdbcTemplate);

        when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenReturn(1);

        userDaoStorage.removeFriend(user.getId(), 2);

        verify(jdbcTemplate, times(1)).update(anyString(), anyInt(), anyInt());
    }

    @Test
    void testAddFriendWhenCalledThenFriendIsAdded() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        userDaoStorage = new UserDaoStorageImpl(jdbcTemplate);

        when(jdbcTemplate.update(anyString(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(1);

        userDaoStorage.addFriend(user.getId(), 2);

        verify(jdbcTemplate, times(1)).update(anyString(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testSearchForSameFriendsWhenCalledThenReturnListOfCommonFriends() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        userDaoStorage = new UserDaoStorageImpl(jdbcTemplate);

        when(jdbcTemplate.queryForRowSet(anyString(), anyInt(), anyInt())).thenReturn(mock(SqlRowSet.class));
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyInt())).thenReturn(user);

        List<User> commonFriends = userDaoStorage.searchForSameFriends(user.getId(), 2);

        assertNotNull(commonFriends);
        assertEquals(0, commonFriends.size());
    }
}