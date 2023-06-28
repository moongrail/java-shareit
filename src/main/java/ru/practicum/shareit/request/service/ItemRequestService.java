package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getItemRequests(Long userId);

    ItemRequestDto getItemRequest(Long userId, Long requestId);

    List<ItemRequestDto> getAllItemRequests(Long userId, Long from, Long size);

    ItemRequestDto addItemRequest(Long userId, ItemRequestPost itemRequestPost);
}
