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
import java.sql.Timestamp;
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
                .orElseThrow(()-> new ItemNotFoundException("Такой предмет не существует"));

        User booker = userRepository
                .findById(userId)
                .orElseThrow(()-> new UserNotFoundException("Пользователь не найден"));

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

        Booking savedBooking = bookingRepository.save(booking);

        return toBookingDto(savedBooking);
    }

    private boolean checkItemHaveUser(Long userId, BookingCreateDto bookingCreateDto) {
        return  itemRepository
                .findById(bookingCreateDto.getItemId())
                .get()
                .getOwner()
                .equals(userId);
    }

    private boolean checkTimestampBooking(Timestamp start, Timestamp end) {
        boolean startAfterEnd = start.after(end);
        boolean timestampsNotNull = start != null && end != null;
        return startAfterEnd && timestampsNotNull;
    }

    @Override
    @Transactional
    public BookingDto patch(Long bookingId, Long userId, BookingDto bookingDto) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена"));

        if (!booking.getBooker().getId().equals(userId)) {
            throw new BookingAuthException("Пользователь не автор брони");
        }

        if (!checkTimestampBooking(bookingDto.getStart(),bookingDto.getEnd())) {
            throw new BookingTimestampException("Ошибка в датах брони");
        }

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        if (bookingDto.getStatus() != null) {
            if (bookingDto.getStatus() == BookingStatus.APPROVED && !booking.getItem().isAvailable()) {
                throw new ItemParameterException("Вещь недоступна");
            }
            booking.setStatus(bookingDto.getStatus());
        }

        Booking savedBooking = bookingRepository.save(booking);
        return toBookingDto(savedBooking);
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
