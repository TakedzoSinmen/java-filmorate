package ru.yandex.practicum.exception;

public class UserAlreadyLikedFilmException extends RuntimeException {
    public UserAlreadyLikedFilmException(String s) {
        super(s);
    }
}
