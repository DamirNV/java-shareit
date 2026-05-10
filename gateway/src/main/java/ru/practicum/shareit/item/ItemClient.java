package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private final String serverUrl;

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplate rest) {
        super(rest);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
        return post(serverUrl + "/items", userId, itemDto);
    }

    public ResponseEntity<Object> update(Long itemId, Long userId, ItemDto itemDto) {
        return patch(serverUrl + "/items/{itemId}", userId, Map.of("itemId", itemId), itemDto);
    }

    public ResponseEntity<Object> findById(Long itemId, Long userId) {
        return get(serverUrl + "/items/{itemId}", userId, Map.of("itemId", itemId));
    }

    public ResponseEntity<Object> findAllByOwner(Long userId) {
        return get(serverUrl + "/items", userId);
    }

    public ResponseEntity<Object> search(String text) {
        return get(serverUrl + "/items/search?text={text}", null, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post(serverUrl + "/items/{itemId}/comment", userId, Map.of("itemId", itemId), commentDto);
    }
}