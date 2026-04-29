package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemWithBookingsDto> findAllByOwner(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        List<Item> items = itemRepository.findAllByOwner(ownerId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> allBookings = bookingRepository.findByItemIdIn(itemIds);
        Map<Long, List<Booking>> bookingsByItem = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());

                    // lastBooking — только APPROVED и законченные
                    Booking lastBooking = itemBookings.stream()
                            .filter(b -> b.getEnd().isBefore(now) && b.getStatus() == BookingStatus.APPROVED)
                            .max((b1, b2) -> b1.getEnd().compareTo(b2.getEnd()))
                            .orElse(null);

                    // nextBooking — только APPROVED и будущие
                    Booking nextBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now) && b.getStatus() == BookingStatus.APPROVED)
                            .min((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                            .orElse(null);

                    List<CommentDto> comments = commentsByItem.getOrDefault(item.getId(), List.of()).stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());

                    return mapToItemWithBookingsDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemWithBookingsDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        LocalDateTime now = LocalDateTime.now();

        // lastBooking — только APPROVED и законченные
        Booking lastBooking = bookingRepository.findLastBookingByItemId(id, now).orElse(null);

        // nextBooking — только APPROVED и будущие
        Booking nextBooking = bookingRepository.findNextBookingByItemId(id, now).orElse(null);

        List<CommentDto> comments = commentRepository.findByItemId(id).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return mapToItemWithBookingsDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }

        Item item = ItemMapper.toItem(itemDto, ownerId);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long itemId, Long ownerId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!ownerId.equals(existingItem.getOwner())) {
            throw new NotOwnerException("Пользователь с id " + ownerId + " не является владельцем вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или аренда ещё не завершена");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemWithBookingsDto mapToItemWithBookingsDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        BookingShortDto lastBookingDto = null;
        BookingShortDto nextBookingDto = null;

        if (lastBooking != null) {
            lastBookingDto = new BookingShortDto(
                    lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getBooker().getName()
            );
        }

        if (nextBooking != null) {
            nextBookingDto = new BookingShortDto(
                    nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getBooker().getName()
            );
        }

        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingDto,
                nextBookingDto,
                comments
        );
    }
}