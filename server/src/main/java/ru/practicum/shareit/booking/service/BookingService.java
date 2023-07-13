package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto patch(Long bookingId, Long userId, Boolean approved);

    void deleteBookingById(Long id);

    BookingDto getBookingByIdAndBooker(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getOwnerBookings(Long userId, String state, Integer from, Integer size);
}
