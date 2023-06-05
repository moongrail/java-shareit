package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapperDto;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotHeaderUserId;
import ru.practicum.shareit.exceptions.ItemParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ItemNotHeaderUserId("Заголовок айди юзера не найден");
        }

        Optional<Item> save = itemStorage.save(userId, itemDto);

        if (save.isPresent()) {
            return ItemMapperDto.toItemDto(save.get());
        }

        throw new ItemParameterException("Ошибка создания");
    }

    @Override
    public ItemDto patch(Long itemId, Long userId, ItemDto itemDto) {
        if (userId == null || userId == null) {
            throw new ItemParameterException("Заголовки не найдены");
        }

        if (!itemStorage.findById(itemId).get().getOwner().equals(userId)){
            throw new ItemNotFoundException("Вещь не найдена у Юзера");
        }

        itemDto.setId(itemId);

        Optional<Item> patch = itemStorage.patch(userId, itemDto);

        if (patch.isPresent()) {
            return ItemMapperDto.toItemDto(patch.get());
        }

        throw new ItemParameterException("Ошибка обновления");
    }

    @Override
    public ItemDto findById(Long id) {
        Optional<Item> byId = itemStorage.findById(id);

        if (byId.isPresent()) {
            return ItemMapperDto.toItemDto(byId.get());
        }

        throw new ItemNotFoundException("Вещь не найдена");
    }

    @Override
    public void delete(Long id) {
        itemStorage.delete(id);
    }

    @Override
    public List<ItemDto> findAllItemByUserId(Long userId) {
        return ItemMapperDto.toListItemDto(itemStorage.findAllItemByUserId(userId));
    }

    @Override
    public List<ItemDto> findByText(String text) {
        return ItemMapperDto.toListItemDto(itemStorage.findByText(text));
    }
}

