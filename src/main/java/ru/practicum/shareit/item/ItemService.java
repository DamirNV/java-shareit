package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> findAllByOwner(Long ownerId);

    ItemWithBookingsDto findById(Long id, Long userId);

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long itemId, Long ownerId, ItemDto itemDto);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}