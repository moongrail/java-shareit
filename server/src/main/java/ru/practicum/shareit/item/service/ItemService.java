package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto, Long requestId);

    List<ItemDto> findAllItemByRequest(Long requestId);

    ItemDto patch(Long itemId, Long userId, ItemDto itemDto);

    ItemResponseDto findById(Long itemId, Long userId);

    void delete(Long id);

    List<ItemResponseDto> findAllItemByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> findByText(String text, Integer from, Integer size);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
