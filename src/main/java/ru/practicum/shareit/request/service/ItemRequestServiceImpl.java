package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;
import ru.practicum.shareit.request.dto.ItemRequestsDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestsDtoMapper.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemRequestDto> getItemRequests(Long userId) {
        checkUserExists(userId);

        return null;
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        ItemRequest itemRequest = itemRequestRepository.findItemRequestByIdAndRequestorId(userId, requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос не найден"));

        return toItemDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Long from, Long size) {
        return null;
    }

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestPost itemRequestPost) {
        checkUserExists(userId);
        ItemRequest itemRequest = fromItemRequestPost(userId, itemRequestPost);

        return toItemDto(itemRequestRepository.save(itemRequest));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
