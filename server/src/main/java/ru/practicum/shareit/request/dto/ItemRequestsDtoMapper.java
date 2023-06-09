package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestsDtoMapper {
    public static ItemRequestDto toItemDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toItemDtoFull(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequest fromItemRequestPost(Long userId, ItemRequestPost itemRequestPost) {
        return ItemRequest.builder()
                .description(itemRequestPost.getDescription())
                .created(LocalDateTime.now())
                .requestorId(userId)
                .build();
    }

    public static List<ItemRequestDto> itemRequestDtos(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestsDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
