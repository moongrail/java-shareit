package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ItemDtoWithUserDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserDto owner;
    private ItemRequest request;
    private BookingItemResponseDto lastBooking;
    private BookingItemResponseDto nextBooking;
    private List<CommentResponseDto> comments;
}
