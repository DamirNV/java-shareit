package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private final String serverUrl;

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplate rest) {
        super(rest);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> create(Long userId, BookingCreateDto bookingCreateDto) {
        return post(serverUrl + "/bookings", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> approve(Long bookingId, Long userId, Boolean approved) {
        return patch(serverUrl + "/bookings/{bookingId}?approved={approved}", userId,
                Map.of("bookingId", bookingId, "approved", approved), null);
    }

    public ResponseEntity<Object> findById(Long bookingId, Long userId) {
        return get(serverUrl + "/bookings/{bookingId}", userId, Map.of("bookingId", bookingId));
    }

    public ResponseEntity<Object> findAllByUser(Long userId, String state) {
        return get(serverUrl + "/bookings?state={state}", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> findAllByOwner(Long userId, String state) {
        return get(serverUrl + "/bookings/owner?state={state}", userId, Map.of("state", state));
    }
}