package ru.practicum.shareit.booking.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BookingRepository extends JpaRepository<Booking, Long> {
    void deleteById(Long id);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner o " +
            "JOIN FETCH b.booker u " +
            "WHERE b.id = :bookId")
    Optional<Booking> getBookingFull(Long bookId);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner o " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner o " +
                    "WHERE b.booker.id = :bookerId")
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE b.booker.id = :bookerId " +
                    "AND b.status = :status")
    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE i.owner.id = :ownerId")
    Page<Booking> findByItemOwnerOrderByStartDesc(Long ownerId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE i.owner.id = :ownerId " +
                    "AND b.status = :status")
    Page<Booking> findByItemOwnerAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE b.booker.id = :bookerId " +
                    "AND b.end < :now")
    Page<Booking> findByBookerIdAndEndLessThanOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE b.booker.id = :bookerId " +
                    "AND b.start > :now")
    Page<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE i.owner.id = :ownerId " +
                    "AND b.end < :now")
    Page<Booking> findByItemOwnerAndEndLessThanOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE i.owner.id = :ownerId " +
                    "AND b.start > :now")
    Page<Booking> findByItemOwnerAndStartGreaterThanOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start < :now " +
            "AND b.end > :now1 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE b.booker.id = :bookerId " +
                    "AND b.start < :now " +
                    "AND b.end > :now1")
    Page<Booking> findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.start < :now " +
            "AND b.end > :now1 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT COUNT(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "JOIN i.owner " +
                    "WHERE i.owner.id = :ownerId " +
                    "AND b.start < :now " +
                    "AND b.end > :now1")
    Page<Booking> findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(Long ownerId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItem_idAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(Long itemId, Long userId);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);

}
