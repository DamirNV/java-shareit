package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingDto bookingDto) {
        // Проверяем пользователя
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        // Проверяем вещь
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingDto.getItemId() + " не найдена"));

        // Проверяем, что вещь доступна для бронирования
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }

        // Владелец не может бронировать свою вещь
        if (item.getOwner().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        // Проверяем даты
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            throw new ValidationException("Дата начала и окончания бронирования обязательны");
        }
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }

        // Создаём бронирование
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        // Проверяем, что пользователь существует
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        // Находим бронирование
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        // Проверяем, что пользователь — владелец вещи
        if (!booking.getItem().getOwner().equals(userId)) {
            throw new NotFoundException("Подтверждать бронирование может только владелец вещи");
        }

        // Проверяем статус бронирования
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано. Текущий статус: " + booking.getStatus());
        }

        // Обновляем статус
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        // Проверяем, что пользователь существует
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        // Находим бронирование
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        // Проверяем, что пользователь — либо автор бронирования, либо владелец вещи
        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().equals(userId);

        if (!isBooker && !isOwner) {
            throw new NotFoundException("Только автор бронирования или владелец вещи могут просматривать бронирование");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllByUser(Long userId, String state) {
        // TODO: реализовать в следующем шаге
        throw new UnsupportedOperationException("Метод пока не реализован");
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state) {
        // TODO: реализовать в следующем шаге
        throw new UnsupportedOperationException("Метод пока не реализован");
    }
}