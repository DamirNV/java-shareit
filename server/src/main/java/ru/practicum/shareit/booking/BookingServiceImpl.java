package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingCreateDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }

        if (item.getOwner().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        LocalDateTime start = bookingCreateDto.getStart();
        LocalDateTime end = bookingCreateDto.getEnd();

        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().equals(userId)) {
            throw new NotOwnerException("Подтверждать бронирование может только владелец вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано. Текущий статус: " + booking.getStatus());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().equals(userId);

        if (!isBooker && !isOwner) {
            throw new NotFoundException("Только автор бронирования или владелец вещи могут просматривать бронирование");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllByUser(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        BookingState bookingState = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, getSort());
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByBooker(userId, now, getSort());
                break;
            case PAST:
                bookings = bookingRepository.findPastByBooker(userId, now, getSort());
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByBooker(userId, now, getSort());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, getSort());
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, getSort());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (itemRepository.findAllByOwner(userId).isEmpty()) {
            return List.of();
        }

        BookingState bookingState = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByOwner(userId, getSort());
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwner(userId, now, getSort());
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwner(userId, now, getSort());
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwner(userId, now, getSort());
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerAndStatus(userId, BookingStatus.WAITING, getSort());
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerAndStatus(userId, BookingStatus.REJECTED, getSort());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "start");
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}