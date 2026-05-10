package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private final String serverUrl;

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplate rest) {
        super(rest);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> findAll() {
        return get(serverUrl + "/users", null);
    }

    public ResponseEntity<Object> findById(Long id) {
        return get(serverUrl + "/users/{id}", null, Map.of("id", id));
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(serverUrl + "/users", null, userDto);
    }

    public ResponseEntity<Object> update(Long id, UserDto userDto) {
        return patch(serverUrl + "/users/{id}", null, Map.of("id", id), userDto);
    }

    public ResponseEntity<Object> delete(Long id) {
        return get(serverUrl + "/users/{id}", null, Map.of("id", id));
    }
}
