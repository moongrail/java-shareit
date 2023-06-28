package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemRequestDto> getItemRequests(Long userId) {
        return null;
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Long from, Long size) {
        return null;
    }

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestPost itemRequestPost) {
        return null;
    }
}
