package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static ru.practicum.shareit.booking.dto.BookingMapperDto.fromBookingDto;
import static ru.practicum.shareit.booking.dto.BookingMapperDto.toBookingDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto getBookingById(Long id) {
        Optional<Booking> byId = bookingRepository.findById(id);

        if (byId.isPresent()) {
            return toBookingDto(byId.get());
        }

        throw new BookingNotFoundException("Booking not found");
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        Item item = itemRepository
                .findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Такой предмет не существует"));

        User booker = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (Boolean.FALSE.equals(item.isAvailable())) {
            throw new BookingParameterException("Предмет недоступен");
        }

//        if (!item.getOwner().equals(userId)) {
//            throw new ItemParameterException("У пользователя нет прав на создание брони");
//        }

        if (checkTimestampBooking(bookingCreateDto.getStart(), bookingCreateDto.getEnd())) {
            throw new BookingTimestampException("Invalid booking start and end timestamps");
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

    private boolean checkItemHaveUser(Long userId, BookingCreateDto bookingCreateDto) {
        return itemRepository
                .findById(bookingCreateDto.getItemId())
                .get()
                .getOwner()
                .equals(userId);
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
        Booking booking = bookingRepository.getBookingFull(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingParameterException("Бронь не находится в состоянии ожидания");
        }

        if (booking.getBooker().getId().equals(userId)) {
            throw new BookingNotFoundException("Не тот юзер. Нельзя редактировать бронь");
        }

        if (!booking.getItem().getOwner().equals(userId)) {
            throw new BookingNotFoundException("У пользователя нет прав на редактирование брони");
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
}
