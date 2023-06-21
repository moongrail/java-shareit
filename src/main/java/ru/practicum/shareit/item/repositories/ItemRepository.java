package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    void deleteById(Long id);

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.available = true" +
            " AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> search(String text);

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.id = :itemId")
    Optional<Item> findByIdFull(Long itemId);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long id);
}
