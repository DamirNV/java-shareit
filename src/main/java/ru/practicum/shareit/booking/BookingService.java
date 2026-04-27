package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);
    BookingDto approve(Long bookingId, Long userId, Boolean approved);
    BookingDto findById(Long bookingId, Long userId);
    List<BookingDto> findAllByUser(Long userId, String state);
    List<BookingDto> findAllByOwner(Long userId, String state);
}
