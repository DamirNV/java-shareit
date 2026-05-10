package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto createDto);

    List<ItemRequestDto> findAllByUser(Long userId);

    List<ItemRequestDto> findAllOther(Long userId);

    ItemRequestDto findById(Long requestId, Long userId);
}
