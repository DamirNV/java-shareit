package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        // TODO: реализовать
        throw new UnsupportedOperationException("Метод пока не реализован");
    }

    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        // TODO: реализовать
        throw new UnsupportedOperationException("Метод пока не реализован");
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        // TODO: реализовать
        throw new UnsupportedOperationException("Метод пока не реализован");
    }

    @Override
    public List<BookingDto> findAllByUser(Long userId, String state) {
        // TODO: реализовать
        throw new UnsupportedOperationException("Метод пока не реализован");
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state) {
        // TODO: реализовать
        throw new UnsupportedOperationException("Метод пока не реализован");
    }
}
