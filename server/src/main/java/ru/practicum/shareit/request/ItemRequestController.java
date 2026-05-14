package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HttpHeadersConstants;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestBody ItemRequestCreateDto createDto
    ) {
        return requestService.create(userId, createDto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUser(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return requestService.findAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllOther(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return requestService.findAllOther(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(
            @PathVariable Long requestId,
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return requestService.findById(requestId, userId);
    }
}