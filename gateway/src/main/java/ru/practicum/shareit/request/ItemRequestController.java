package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HttpHeadersConstants;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody ItemRequestCreateDto createDto
    ) {
        return requestClient.create(userId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUser(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return requestClient.findAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOther(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return requestClient.findAllOther(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @PathVariable Long requestId,
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return requestClient.findById(requestId, userId);
    }
}