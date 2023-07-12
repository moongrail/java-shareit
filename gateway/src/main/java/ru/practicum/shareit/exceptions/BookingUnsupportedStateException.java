package ru.practicum.shareit.exceptions;

public class BookingUnsupportedStateException extends RuntimeException {
    public BookingUnsupportedStateException(String message) {
        super(message);
    }
}
