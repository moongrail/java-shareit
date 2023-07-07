package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@PropertySource("classpath:headers.properties")
@PropertySource("classpath:application.properties")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                                     @PathVariable Long bookingId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.getBookingByIdAndBooker(userId, bookingId));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookingDto> addBooking(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                                 @RequestBody @Valid BookingCreateDto bookingCreateDto,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(BookingDto.builder().build());
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.create(userId, bookingCreateDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> patchBooking(@PathVariable Long bookingId,
                                                   @RequestHeader(name = "${headers.user.id.name}") Long userId,
                                                   @RequestParam(name = "approved", required = false) Boolean approved) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.patch(bookingId, userId, approved));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBookingById(@PathVariable Long bookingId) {
        bookingService.deleteBookingById(bookingId);
        return ResponseEntity.ok("Удален Booking с id " + bookingId);
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                                            @RequestParam(name = "from", required = false) Integer from,
                                                            @RequestParam(name = "size", required = false) Integer size,
                                                            @RequestParam(name = "state",
                                                                    defaultValue = "ALL") String state) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.getUserBookings(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                                             @RequestParam(name = "from", required = false) Integer from,
                                                             @RequestParam(name = "size", required = false) Integer size,
                                                             @RequestParam(name = "state",
                                                                     defaultValue = "ALL") String state) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.getOwnerBookings(userId, state, from, size));
    }
}

