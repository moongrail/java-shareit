package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    public static final long TEST_ID = 1L;
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private BookingDto bookingDto;
    private Booking booking;
    private Item item;
    private User user;

    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user = User.builder().id(TEST_ID).build();
        item = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(user)
                .build();
        bookingDto = BookingDto.builder()
                .id(TEST_ID)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();
        booking = Booking.builder()
                .id(TEST_ID)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(TEST_ID)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .build();
    }

    @Test
    void getBookingByIdAndBooker_whenInvoked_thenReturnBooking() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(TEST_ID)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(item));

        BookingDto result = bookingService.getBookingByIdAndBooker(TEST_ID, user.getId());

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        assertEquals(bookingDto.getEnd().getSecond(), result.getEnd().getSecond());
        assertEquals(bookingDto.getStart().getSecond(), result.getStart().getSecond());
        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        verify(bookingRepository, times(1)).getBookingFull(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
    }

    @Test
    void getBookingByIdAndBooker_whenItemNotFound_thenThrowItemNotFoundException() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(TEST_ID)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(TEST_ID, user.getId()));

        assertEquals("Вещь не найдена", exception.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void getBookingByIdAndBooker_whenBookingNotFound_thenThrowBookingNotFoundException() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(TEST_ID)).thenReturn(Optional.empty());
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(TEST_ID, user.getId()));

        assertEquals("Бронирование не найдено", exception.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(0)).findById(TEST_ID);
    }

    @Test
    void getBookingByIdAndBooker_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(TEST_ID, user.getId()));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).existsById(TEST_ID);
        verify(bookingRepository, times(0)).getBookingFull(TEST_ID);
        verify(itemRepository, times(0)).findById(TEST_ID);
    }

    @Test
    void getBookingByIdAndBooker_whenUserNotOwner_thenThrowUserNotFoundException() {
        Item badItem = Item.builder()
                .id(TEST_ID)
                .owner(User.builder().id(2L).build())
                .build();

        Booking badBooking = Booking.builder()
                .id(TEST_ID)
                .booker(User.builder().id(2L).build())
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(TEST_ID)).thenReturn(Optional.of(badBooking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(badItem));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(TEST_ID, TEST_ID));

        assertEquals("Ошибка параметров пользователя и бронирования", exception.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
    }

    @Test
    void getBookingByIdAndBooker_whenUserOwnerError_thenThenThrowUserNotFoundException() {
        Item badItem = Item.builder()
                .id(TEST_ID)
                .owner(User.builder().id(2L).build())
                .build();

        Booking badBooking = Booking.builder()
                .id(TEST_ID)
                .booker(User.builder().id(2L).build())
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(TEST_ID)).thenReturn(Optional.of(badBooking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(badItem));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(TEST_ID, TEST_ID));

        assertEquals("Ошибка параметров пользователя и бронирования", exception.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
    }

    @Test
    void create_whenInvoked_thenBookingCreate() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.create(TEST_ID, bookingCreateDto);

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        assertEquals(bookingDto.getEnd().getSecond(), result.getEnd().getSecond());
        assertEquals(bookingDto.getStart().getSecond(), result.getStart().getSecond());
        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        verify(bookingRepository, times(1)).save(any());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void create_whenUserNotFound_thenThrowUserNotFoundException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.create(TEST_ID, bookingCreateDto));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void create_whenItemNotFound_thenThrowItemNotFoundException() {
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(TEST_ID, bookingCreateDto));

        assertEquals("Такой предмет не существует", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(userRepository, times(0)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
    }

    @Test
    void create_whenAvailableFalse_thenThrowBookingParameterException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(false)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        BookingParameterException exception = assertThrows(BookingParameterException.class,
                () -> bookingService.create(TEST_ID, bookingCreateDto));

        assertEquals("Предмет недоступен", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void create_whenOwnerError_thenThrowUserNotFoundException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(1L).build())
                .build();

        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.create(TEST_ID, bookingCreateDto));

        assertEquals("У пользователя нет прав на создание брони", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void create_whenTimeError_thenThrowBookingTimestampException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto badBooking = BookingCreateDto.builder()
                .itemId(TEST_ID)
                .end(now)
                .start(now)
                .build();


        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        BookingTimestampException exception = assertThrows(BookingTimestampException.class,
                () -> bookingService.create(TEST_ID, badBooking));

        assertEquals("Неправильное время бронирования", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void patch_whenApprovedFalse_thenReturnPatchBooking() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(User.builder()
                        .id(TEST_ID)
                        .email("email2@email.com").name("name2")
                        .build())
                .build();
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        Booking bookingSaved = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .status(BookingStatus.REJECTED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();


        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        BookingDto result = bookingService.patch(TEST_ID, TEST_ID, Boolean.FALSE);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void patch_whenApprovedTrue_thenReturnPatchBooking() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(User.builder()
                        .id(TEST_ID)
                        .email("email2@email.com").name("name2")
                        .build())
                .build();
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        Booking bookingSaved = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .status(BookingStatus.APPROVED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();


        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        BookingDto result = bookingService.patch(TEST_ID, TEST_ID, Boolean.TRUE);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void patch_whenItemErrorIdUser_thenThrowBookingParameterException() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(userPatch)
                .build();
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        BookingParameterException exception = assertThrows(BookingParameterException.class,
                () -> bookingService.patch(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("У пользователя нет прав на редактирование брони", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void patch_whenBookingErrorIdUser_thenThrowBookingNotFoundException() {
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.patch(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("Не тот юзер. Нельзя редактировать бронь", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void patch_whenBookingStatusNotWaiting_thenThrowBookingParameterException() {
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        BookingParameterException exception = assertThrows(BookingParameterException.class,
                () -> bookingService.patch(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("Бронь не находится в состоянии ожидания", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void patch_whenBookingNotFound_thenThrowBookingNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.patch(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("Бронь не найдена", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void deleteBookingById_whenInvoked_thenDeleted() {
        when(bookingRepository.existsById(eq(TEST_ID))).thenReturn(true);

        bookingService.deleteBookingById(TEST_ID);

        verify(bookingRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteBookingById_whenBookingNotExist_thenThrowBookingNotFoundException() {
        when(bookingRepository.existsById(eq(TEST_ID))).thenReturn(false);
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.deleteBookingById(TEST_ID));

        assertEquals("Брони не существует.", exception.getMessage());
        verify(bookingRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void getUserBookings_whenInvokedStatePast_thenHaveListOneBooking() {
        BookingState bookingState = BookingState.PAST;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getUserBookings_whenInvokedStateCurrent_thenHaveListOneBooking() {
        BookingState bookingState = BookingState.CURRENT;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getUserBookings_whenInvokedStateFuture_thenHaveListOneBooking() {
        BookingState bookingState = BookingState.FUTURE;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getUserBookings_whenInvokedStateWaiting_thenHaveListOneBooking() {
        BookingState bookingState = BookingState.WAITING;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getUserBookings_whenInvokedStateRejected_thenHaveListOneBooking() {
        BookingState bookingState = BookingState.REJECTED;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getUserBookings_whenInvokedStateAll_thenHaveListOneBooking() {
        BookingState bookingState = BookingState.ALL;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getUserBookings_whenInvalidState_thenThrowBookingUnsupportedStateException() {
        String state = "INVALID";
        when(userRepository.existsById(anyLong())).thenReturn(true);
        BookingUnsupportedStateException exception = assertThrows(BookingUnsupportedStateException.class,
                () -> bookingService.getUserBookings(TEST_ID, state, 0, 2));

        assertEquals("Unknown state: " + state, exception.getMessage());
        verify(bookingRepository, times(0))
                .findByBookerIdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStateCurrent_thenReturnListOneBooking() {
        BookingState bookingState = BookingState.CURRENT;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStatePast_thenReturnListOneBooking() {
        BookingState bookingState = BookingState.PAST;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndEndLessThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndEndLessThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStateFuture_thenReturnListOneBooking() {
        BookingState bookingState = BookingState.FUTURE;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStateWaiting_thenReturnListOneBooking() {
        BookingState bookingState = BookingState.WAITING;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStateRejected_thenReturnListOneBooking() {
        BookingState bookingState = BookingState.REJECTED;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStateAll_thenReturnListOneBooking() {
        BookingState bookingState = BookingState.ALL;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(TEST_ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStateNotExist_thenThrowBookingUnsupportedStateException() {
        String state = "INVALID";

        when(userRepository.existsById(anyLong())).thenReturn(true);

        BookingUnsupportedStateException exception = assertThrows(BookingUnsupportedStateException.class,
                () -> bookingService.getOwnerBookings(TEST_ID, state, 0, 2));

        assertEquals("Unknown state: " + state, exception.getMessage());
        verify(bookingRepository, times(0))
                .findByItemOwnerOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }
}