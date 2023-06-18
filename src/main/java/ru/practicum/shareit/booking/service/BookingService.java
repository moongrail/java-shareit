package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto getBookingById(Long id);

    BookingDto create(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto patch(Long bookingId, Long userId,Boolean approved);

    void deleteBookingById(Long id);
}
