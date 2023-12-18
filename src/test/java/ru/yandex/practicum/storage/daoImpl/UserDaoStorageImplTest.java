package ru.yandex.practicum.storage.daoImpl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.api.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDaoStorageImplTest {

    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private User user;

    @BeforeEach
    void set() {
        userStorage = new UserDaoStorageImpl(jdbcTemplate);

        user = new User(0, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), Collections.emptyList());
    }

    @Test
    @DirtiesContext
    public void testFindUserById() {
        set();
        userStorage.addUser(user);

        User savedUser = userStorage.getUserById(1).get();

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    @DirtiesContext
    public void testFindAll() {
        set();
        userStorage.addUser(user);

        List<User> savedUsers = new ArrayList<>(userStorage.getUsers());

        assertThat(savedUsers.size())
                .isEqualTo(1);
    }

    @Test
    @DirtiesContext
    public void testCreate() {
        set();

        User newUser = userStorage.addUser(user);

        assertThat(newUser.getId())
                .isEqualTo(1);
    }

    @Test
    @DirtiesContext
    public void testUpdate() {
        set();
        userStorage.addUser(user);
        user.setName("No name");

        userStorage.updateUser(user);

        assertThat(userStorage.getUserById(1).get().getName())
                .isEqualTo("No name");
    }
}
