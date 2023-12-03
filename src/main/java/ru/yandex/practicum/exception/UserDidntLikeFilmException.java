package ru.yandex.practicum.exception;

public class UserDidntLikeFilmException extends RuntimeException {
    public UserDidntLikeFilmException(String s) {
        super(s);
    }
}
