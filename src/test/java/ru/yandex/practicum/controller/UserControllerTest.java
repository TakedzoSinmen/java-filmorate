package ru.yandex.practicum.controller;

import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.exception.CustomValidationException;
import ru.yandex.practicum.model.User;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final UserController userController = new UserController();

    User user1 = new User(1, "", "", "", LocalDate.of(2222, 10,10));

    @Test
    void givenWrongUser_whenPostRequest_thenThrowException() {
        assertThrows(CustomValidationException.class, () -> userController.addUser(user1));
    }

    @Test
    void givenWrongUser_whenPutRequest_thenThrowException() {
        assertThrows(CustomValidationException.class, () -> userController.updateUser(user1));
    }
}