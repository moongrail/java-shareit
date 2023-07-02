package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.BookingParameterException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class})
@AutoConfigureMockMvc
class BookingControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private BookingDto responseDto;
    private BookingCreateDto createDto;

    @BeforeEach
    void setUp() {
        bookingDto = getTestBookingDto();
        responseDto = getResponseDto();
        createDto = getCreateDto();
    }

    @Test
    @SneakyThrows
    void getBookingById_whenInvoke_thenStatusOk() {
        when(bookingService.getBookingByIdAndBooker(eq(1L), eq(1L))).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getBookingById_whenInvokeNotExistId_thenStatusNotFound() {
        when(bookingService.getBookingByIdAndBooker(eq(0L), eq(0L)))
                .thenThrow(BookingNotFoundException.class);
        mockMvc.perform(get("/bookings/{bookingId}", 0L)
                        .header(HEADER_USER_ID, 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void addBooking_whenInvoke_StatusCreated() {
        when(bookingService.create(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addBooking_whenInvokeWithoutItemId_StatusBadRequest() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .end(LocalDateTime.now().plusHours(1))
                .start(LocalDateTime.now())
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(BookingParameterException.class);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addBooking_whenInvokeWithoutStartEndDate_StatusBadRequest() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(BookingParameterException.class);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addBooking_whenInvokeEmptyData_StatusBadRequest() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(BookingParameterException.class);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void patchBooking_whenInvokeFalse_ThenStatusOk() {
        when(bookingService.patch(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .param("approved", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void patchBooking_whenInvokeTrue_ThenStatusOk() {
        when(bookingService.patch(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void patchBooking_whenInvokeWithBookingIdNotExist_ThenStatusBadRequest() {
        when(bookingService.patch(anyLong(), anyLong(), anyBoolean())).thenThrow(BookingNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", 999L)
                        .header(HEADER_USER_ID, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void patchBooking_whenInvokeWithUserNotExist_ThenStatusNotFound() {
        when(bookingService.patch(anyLong(), anyLong(), anyBoolean())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HEADER_USER_ID, 0L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void deleteBookingById_whenInvokeCorrect_thenStatusDeleted() {
        mockMvc.perform(delete("/bookings/{bookingId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteBookingById_whenInvokeNotExistId_thenStatusNotFound() {
        doThrow(BookingNotFoundException.class).when(bookingService).deleteBookingById(0L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{bookingId}", 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getUserBookings_whenInvokedEmptyList_thenStatusOk() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();

    }

    @Test
    @SneakyThrows
    void getUserBookings_whenInvokedList1Booking_thenStatusOk() {
        List<BookingDto> bookingDtoList = getBookingDtos();

        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoList)))
                .andDo(print())
                .andReturn();

    }

    @Test
    @SneakyThrows
    void getUserBookings_whenInvokedNotExistUser_thenStatusNotFound() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 0L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getOwnerBookings_whenInvoked1Booking_thenStatusOk() {
        List<BookingDto> bookingDtoList = getBookingDtos();

        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoList)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getOwnerBookings_whenInvokedEmptyBooking_thenStatusOk() {

        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getOwnerBookings_whenInvokedEmptyList_thenStatusOk() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getOwnerBookings_whenInvokedNotExistUser_thenStatusNotFound() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 0L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    private List<BookingDto> getBookingDtos() {
        return List.of(bookingDto);
    }

    private static BookingDto getTestBookingDto() {
        return BookingDto.builder()
                .id(1L)
                .end(LocalDateTime.now().plusHours(1))
                .start(LocalDateTime.now())
                .booker(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@email.com")
                        .build())
                .item(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("item description")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("owner")
                                .email("owner@email.com")
                                .build())
                        .build())
                .status(BookingStatus.WAITING)
                .build();
    }

    private static BookingDto getResponseDto() {
        return BookingDto.builder()
                .id(1L)
                .item(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("item description")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("owner")
                                .email("owner@email.com")
                                .build())
                        .build())
                .booker(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@email.com")
                        .build())
                .end(LocalDateTime.now().plusDays(2))
                .start(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.WAITING)
                .build();
    }

    private static BookingCreateDto getCreateDto() {
        return BookingCreateDto.builder()
                .end(LocalDateTime.now().plusDays(2))
                .start(LocalDateTime.now().plusHours(1))
                .itemId(1L)
                .build();
    }
}