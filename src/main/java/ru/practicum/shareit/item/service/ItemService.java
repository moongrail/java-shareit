package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);
    ItemDto patch(Long itemId, Long userId, ItemDto itemDto);
    ItemDto findById(Long id);
    void delete(Long id);
    List<ItemDto> findAllItemByUserId(Long userId);
    List<ItemDto> findByText(String text);
}
