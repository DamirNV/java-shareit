package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void shouldCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель 600Вт");
        itemDto.setAvailable(true);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Дрель");
        responseDto.setDescription("Мощная дрель 600Вт");
        responseDto.setAvailable(true);

        when(itemClient.create(anyLong(), any(ItemDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void shouldNotCreateItemWithoutName() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("Мощная дрель 600Вт");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateItemWithoutDescription() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateItemWithoutAvailable() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель 600Вт");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateItem() throws Exception {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Дрель Pro");

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Дрель Pro");
        responseDto.setDescription("Мощная дрель 600Вт");
        responseDto.setAvailable(true);

        when(itemClient.update(eq(1L), anyLong(), any(ItemDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель Pro"));
    }

    @Test
    void shouldFindItemById() throws Exception {
        when(itemClient.findById(1L, 1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L, "name", "Дрель")));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAllByOwner() throws Exception {
        when(itemClient.findAllByOwner(1L)).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSearchItems() throws Exception {
        when(itemClient.search("дрель")).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличная вещь!");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);
        responseDto.setText("Отличная вещь!");
        responseDto.setAuthorName("Иван");
        responseDto.setCreated(LocalDateTime.now());

        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Отличная вещь!"));
    }

    @Test
    void shouldNotAddEmptyComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}