package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingStateTest {

    @ParameterizedTest
    @ValueSource(strings = {"CURRENT", "FUTURE", "PAST", "REJECTED", "WAITING"})
    public void testFromValidString(String stringState) {
        Optional<BookingState> state = BookingState.from(stringState);
        assertTrue(state.isPresent());
    }

    @Test
    public void testFromInvalidString() {
        Optional<BookingState> state = BookingState.from("INVALID");
        assertTrue(state.isEmpty());
    }
}