package ru.practicum.shareit.exceptions;

public class BookingAuthException extends RuntimeException {
    public BookingAuthException(String message) {
        super(message);
    }
}
