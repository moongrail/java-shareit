package ru.practicum.shareit.exceptions;

public class UserUniqueEmailException extends RuntimeException{
    public UserUniqueEmailException(String message) {
        super(message);
    }
}
