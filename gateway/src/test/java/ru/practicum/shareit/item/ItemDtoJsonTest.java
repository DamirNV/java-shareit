package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void shouldSerializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель 600Вт");
        itemDto.setAvailable(true);
        itemDto.setRequestId(5L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Мощная дрель 600Вт");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
    }

    @Test
    void shouldSerializeItemDtoWithoutRequestId() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель 600Вт");
        itemDto.setAvailable(true);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).doesNotHaveJsonPathValue("$.requestId");
    }

    @Test
    void shouldDeserializeItemDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"Дрель\",\"description\":\"Мощная дрель 600Вт\",\"available\":true,\"requestId\":5}";

        ItemDto result = json.parse(content).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Мощная дрель 600Вт");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(5L);
    }

    @Test
    void shouldDeserializeItemDtoWithoutRequestId() throws Exception {
        String content = "{\"name\":\"Дрель\",\"description\":\"Мощная дрель 600Вт\",\"available\":true}";

        ItemDto result = json.parse(content).getObject();

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Мощная дрель 600Вт");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isNull();
    }
}