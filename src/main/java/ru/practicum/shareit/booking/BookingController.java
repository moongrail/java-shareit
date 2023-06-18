package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(bookingDto);
    }

    @PostMapping()
    public ResponseEntity<BookingDto> addBooking(@RequestHeader(name = HEADER_USER_ID) Long userId,
                                                 @RequestBody @Valid BookingCreateDto bookingCreateDto,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(BookingDto.builder().build());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.create(userId, bookingCreateDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> patchBooking(@PathVariable Long bookingId,
                                                   @RequestHeader(name = HEADER_USER_ID) Long userId,
                                                   @RequestParam(name = "approved", required = false) Boolean approved,
                                                   @RequestBody BookingDto bookingDto) {
        if (approved != null && approved) {
            bookingDto.setStatus(BookingStatus.APPROVED);
        }

        BookingDto updatedBookingDto = bookingService.patch(bookingId, userId, bookingDto);
        return ResponseEntity.ok(updatedBookingDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookingById(@PathVariable Long id) {
        bookingService.deleteBookingById(id);
        return ResponseEntity.ok("Booking with id " + id + " has been deleted successfully");
    }
}

