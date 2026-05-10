package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private final String serverUrl;

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplate rest) {
        super(rest);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestCreateDto createDto) {
        return post(serverUrl + "/requests", userId, createDto);
    }

    public ResponseEntity<Object> findAllByUser(Long userId) {
        return get(serverUrl + "/requests", userId);
    }

    public ResponseEntity<Object> findAllOther(Long userId) {
        return get(serverUrl + "/requests/all", userId);
    }

    public ResponseEntity<Object> findById(Long requestId, Long userId) {
        return get(serverUrl + "/requests/{requestId}", userId, Map.of("requestId", requestId));
    }
}