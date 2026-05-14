package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                      Map<String, Object> parameters, Object body) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> response;
        try {
            if (parameters != null && !parameters.isEmpty()) {
                response = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return response;
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}