package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapperDto {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .request(item.getRequest())
                .build();
    }

    public static Item fromItemDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.isAvailable())
                .request(itemDto.getRequest())
                .build();
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapperDto::toItemDto)
                .collect(Collectors.toList());
    }
}
