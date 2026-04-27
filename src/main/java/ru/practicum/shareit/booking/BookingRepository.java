package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Для владельца вещи — все бронирования его вещей
    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId")
    List<Booking> findAllByOwner(@Param("ownerId") Long ownerId, Sort sort);

    // Для владельца вещи — CURRENT (текущие)
    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.start <= :now AND b.end > :now")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Для владельца вещи — PAST (прошедшие)
    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.end < :now")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Для владельца вещи — FUTURE (будущие)
    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.start > :now")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Для владельца вещи — WAITING
    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.status = :status")
    List<Booking> findByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status, Sort sort);

    // Для пользователя — все бронирования
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    // Для пользователя — CURRENT (текущие)
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end > :now")
    List<Booking> findCurrentByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    // Для пользователя — PAST (прошедшие)
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findPastByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    // Для пользователя — FUTURE (будущие)
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :now")
    List<Booking> findFutureByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    // Для пользователя — WAITING или REJECTED
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    // Вспомогательные методы
    List<Booking> findByItemId(Long itemId);
    Optional<Booking> findByIdAndBookerId(Long bookingId, Long bookerId);
    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long ownerId);
    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    // Найти последнее завершённое бронирование для вещи
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.end < :now AND b.status = 'APPROVED' ORDER BY b.end DESC")
    Optional<Booking> findLastBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    // Найти следующее подтверждённое бронирование для вещи
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :now AND b.status = 'APPROVED' ORDER BY b.start ASC")
    Optional<Booking> findNextBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    // Найти все бронирования для списка вещей
    List<Booking> findByItemIdIn(List<Long> itemIds);
}