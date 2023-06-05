package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapperDto;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final UserStorage userStorage;
    private static final Map<Long, Item> items = new HashMap<>();
    private static Long index = 1L;

    @Override
    public Optional<Item> save(Long userId, ItemDto itemDto) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        Item item = ItemMapperDto.fromItemDto(itemDto);
        item.setOwner(userId);
        item.setId(index);
        items.put(item.getId(), item);
        index++;

        return Optional.of(item);
    }

    @Override
    public Optional<Item> patch(Long id, ItemDto itemDto) {
        Optional<Item> optionalItem = findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.isAvailable()) {
                item.setAvailable(itemDto.isAvailable());
            }
            items.put(item.getId(), item);

            return Optional.of(item);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = items.get(id);
        return Optional.ofNullable(item);
    }

    @Override
    public void delete(Long id) {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Товар не найден");
        }
        items.remove(id);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findByText(String text) {
        List<Item> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getName().contains(text) || item.getDescription().contains(text)) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }
}
