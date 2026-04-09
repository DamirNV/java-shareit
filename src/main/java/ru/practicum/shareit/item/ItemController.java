package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // Добавление новой вещи
    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemService.create(ownerId, itemDto);
    }

    // Редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.update(itemId, ownerId, itemDto);
    }

    // Просмотр информации о конкретной вещи
    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    // Просмотр списка всех вещей владельца
    @GetMapping
    public List<ItemDto> findAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        return itemService.findAllByOwner(ownerId);
    }

    // Поиск вещей по тексту
    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
