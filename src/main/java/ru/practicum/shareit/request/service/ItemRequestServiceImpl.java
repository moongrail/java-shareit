package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.util.PaginationUtil;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestsDtoMapper.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Override
    public List<ItemRequestDto> getItemRequests(Long userId) {
        checkUserExists(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequestDtos(itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId));
        itemRequestDtos.forEach(i -> i.setItems(itemService.findAllItemByRequest(i.getId())));

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос не найден"));
        ItemRequestDto itemRequestDto = toItemDto(itemRequest);
        itemRequestDto.setItems(itemService.findAllItemByRequest(itemRequestDto.getId()));

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long requesterId, Integer from, Integer size) {
        checkUserExists(requesterId);
        Pageable paginationWithSortDesc = PaginationUtil.getPaginationWithSortDesc(from, size);
        Page<ItemRequest> findItemRequests = itemRequestRepository.findAllByRequestorIdNot(requesterId,
                paginationWithSortDesc);
        List<ItemRequestDto> itemRequestDtos = itemRequestDtos(findItemRequests.stream()
                .collect(Collectors.toList()));
        itemRequestDtos.forEach(s -> s.setItems(itemService.findAllItemByRequest(s.getId())));

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto addItemRequest(Long requesterId, ItemRequestPost itemRequestPost) {
        checkUserExists(requesterId);
        ItemRequest itemRequest = fromItemRequestPost(requesterId, itemRequestPost);

        return toItemDto(itemRequestRepository.save(itemRequest));
    }

    private void checkUserExists(Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
