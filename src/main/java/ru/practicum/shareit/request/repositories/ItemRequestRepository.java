package ru.practicum.shareit.request.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest,Long> {
    Optional<ItemRequest> findItemRequestByIdAndRequestorId(Long userId, Long requestId);

}
