package ru.practicum.shareit.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.user.UserClientController;

@RestControllerAdvice(basePackageClasses = {UserClientController.class, BookingController.class})
public class ErrorHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ItemNotHeaderUserId.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleItemNotHeaderUserId(ItemNotHeaderUserId ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ItemParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleItemParameterException(ItemParameterException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserUniqueEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserUniqueEmailException(UserUniqueEmailException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserParameterException(UserParameterException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(BookingNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ItemRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestNotFoundException(ItemRequestNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(BookingTimestampException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingTimestampException(BookingTimestampException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(PaginationParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePaginationParameterException(PaginationParameterException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(BookingParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingParameterException(BookingParameterException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(BookingUnsupportedStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingUnsupportedStateException(BookingUnsupportedStateException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}
