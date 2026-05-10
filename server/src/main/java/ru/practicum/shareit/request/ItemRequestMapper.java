package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemShortDto> items) {
        if (request == null) {
            return null;
        }
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items != null ? items : List.of()
        );
    }
}