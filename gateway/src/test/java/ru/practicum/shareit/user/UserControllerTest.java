package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void shouldCreateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Иван Петров");
        userDto.setEmail("ivan@example.com");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Иван Петров");
        responseDto.setEmail("ivan@example.com");

        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Иван Петров");
        userDto.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateUserWithoutName() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("ivan@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindUserById() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Иван Петров");
        responseDto.setEmail("ivan@example.com");

        when(userClient.findById(1L)).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Петров"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setName("Иван Сидоров");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Иван Сидоров");
        responseDto.setEmail("ivan@example.com");

        when(userClient.update(eq(1L), any(UserDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Сидоров"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        when(userClient.delete(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAllUsers() throws Exception {
        when(userClient.findAll()).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }
}