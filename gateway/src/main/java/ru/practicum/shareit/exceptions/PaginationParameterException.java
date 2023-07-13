package ru.practicum.shareit.exceptions;

public class PaginationParameterException extends RuntimeException {
    public PaginationParameterException(String message) {
        super(message);
    }
}
