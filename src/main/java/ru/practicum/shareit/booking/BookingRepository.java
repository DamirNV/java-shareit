package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Найти все бронирования вещи
    List<Booking> findByItemId(Long itemId);

    // Найти бронирование по id и id бронирующего
    Optional<Booking> findByIdAndBookerId(Long bookingId, Long bookerId);

    // Найти бронирование по id и id владельца вещи
    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long ownerId);

    // Проверить, есть ли у пользователя завершённое бронирование вещи
    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, java.time.LocalDateTime now);
}