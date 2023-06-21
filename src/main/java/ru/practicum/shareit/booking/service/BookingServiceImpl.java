package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.dto.BookingMapperDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto getBookingByIdAndBooker(Long userId, Long bookingId) {
        checkUserExist(userId);

        Booking booking = bookingRepository.getBookingFull(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));

        if (!(userId.equals(item.getOwner().getId())) && !(userId.equals(booking.getBooker().getId()))) {
            throw new UserNotFoundException(
                    "Ошибка параметров пользователя и бронирования");
        }

        return toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        Item item = itemRepository
                .findByIdFull(bookingCreateDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Такой предмет не существует"));

        User booker = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (Boolean.FALSE.equals(item.isAvailable())) {
            throw new BookingParameterException("Предмет недоступен");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("У пользователя нет прав на создание брони");
        }

        if (checkTimestampBooking(bookingCreateDto.getStart(), bookingCreateDto.getEnd())) {
            throw new BookingTimestampException("Неправильное время бронирования");
        }


        Booking booking = fromBookingDto(BookingDto.builder()
                .item(item)
                .booker(booker)
                .end(bookingCreateDto.getEnd())
                .start(bookingCreateDto.getStart())
                .status(BookingStatus.WAITING)
                .build());

        return toBookingDto(bookingRepository.save(booking));
    }

    private boolean checkTimestampBooking(LocalDateTime start, LocalDateTime end) {
        boolean startAfterEnd = start.isAfter(end);
        boolean timestampsNotNull = start == null || end == null;
        boolean equalsTime = start.equals(end);
        return startAfterEnd || timestampsNotNull || equalsTime;
    }

    @Override
    @Transactional
    public BookingDto patch(Long bookingId, Long userId, Boolean approved) {
        checkUserExist(userId);
        Booking booking = bookingRepository.getBookingFull(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingParameterException("Бронь не находится в состоянии ожидания");
        }

        if (booking.getBooker().getId().equals(userId)) {
            throw new BookingNotFoundException("Не тот юзер. Нельзя редактировать бронь");
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingParameterException("У пользователя нет прав на редактирование брони");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void deleteBookingById(Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
        }

        throw new BookingNotFoundException("Брони не существует.");
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        checkUserExist(userId);
        checkValidState(state);
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings;

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(userId,
                        currentTime, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }

        return toListBookingDto(bookings);
    }

    private static void checkValidState(String state) {
        try {
            BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BookingUnsupportedStateException("Unknown state: " + state);
        }
    }

    private void checkUserExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String state) {
        checkUserExist(userId);
        checkValidState(state);
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(userId, currentTime, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerAndEndLessThanOrderByStartDesc(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerAndStartGreaterThanOrderByStartDesc(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerOrderByStartDesc(userId);
                break;
        }

        return toListBookingDto(bookings);
    }
}
