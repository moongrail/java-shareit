package ru.practicum.shareit.request.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Optional<ItemRequest> findItemRequestById(Long requestId);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requesterId);

    Page<ItemRequest> findAllByRequestorIdNot(Long requesterId, Pageable pageable);
}
