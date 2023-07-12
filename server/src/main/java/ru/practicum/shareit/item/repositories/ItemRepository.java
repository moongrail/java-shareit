package ru.practicum.shareit.item.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    void deleteById(Long id);

    @Query(value = "SELECT i FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.available = true" +
            " AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))",
            countQuery = "SELECT COUNT(i) FROM Item i " +
                    "JOIN i.owner o " +
                    "WHERE i.available = true" +
                    " AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
                    " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    Page<Item> searchPage(String text, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.id = :itemId")
    Optional<Item> findByIdFull(Long itemId);

    List<Item> findAllByRequestId(Long requestId);

    Page<Item> findAllByOwnerIdOrderByIdAsc(Long id, Pageable pageable);
}
