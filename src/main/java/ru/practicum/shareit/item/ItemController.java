package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HttpHeadersConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long ownerId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable Long itemId,
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long ownerId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.update(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto findById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> findAllByOwner(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long ownerId
    ) {
        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}