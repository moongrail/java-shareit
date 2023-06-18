package ru.practicum.shareit.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    void deleteById(Long id);
}
