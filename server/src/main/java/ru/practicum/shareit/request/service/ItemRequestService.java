package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getItemRequests(Long requesterId);

    ItemRequestDto getItemRequest(Long requesterId, Long requestId);

    List<ItemRequestDto> getAllItemRequests(Long requesterId, Integer from, Integer size);

    ItemRequestDto addItemRequest(Long requesterId, ItemRequestPost itemRequestPost);
}
