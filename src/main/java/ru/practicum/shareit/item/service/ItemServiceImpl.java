package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotHeaderUserId;
import ru.practicum.shareit.exceptions.ItemParameterException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.item.dto.ItemMapperDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ItemNotHeaderUserId("Заголовок айди юзера не найден");
        } else if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Юзер не найден");
        }

        itemDto.setOwner(userId);
        Item item = fromItemDto(itemDto);

        Item save = itemRepository.save(item);

        return toItemDto(save);
    }

    @Override
    @Transactional
    public ItemDto patch(Long itemId, Long owner, ItemDto itemDto) {
        if (!itemRepository.findById(itemId).get().getOwner().equals(owner)) {
            throw new ItemNotFoundException("Вещь не найдена у Юзера");
        }

        Optional<Item> byId = itemRepository.findById(itemId);

        if (byId.isPresent()) {
            Item itemForUpdate = byId.get();

            if (itemDto.getName() != null) {
                itemForUpdate.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                itemForUpdate.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                itemForUpdate.setAvailable(itemDto.getAvailable());
            }

            Item save = itemRepository.save(itemForUpdate);

            return toItemDto(save);
        }


        throw new ItemParameterException("Ошибка обновления");
    }

    @Override
    public ItemDto findById(Long id) {
        Optional<Item> byId = itemRepository.findById(id);

        if (byId.isPresent()) {
            return toItemDto(byId.get());
        }

        throw new ItemNotFoundException("Вещь не найдена");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Вещь не найдена");
        }

        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> findAllItemByUserId(Long userId) {
        return toListItemDto(itemRepository.findAllByOwnerIs(userId));
    }

    @Override
    public List<ItemDto> findByText(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }

        return toListItemDto(itemRepository.search(text));
    }
}

