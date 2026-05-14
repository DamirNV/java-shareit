package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void shouldSerializeCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.now();

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Отличная вещь!");
        commentDto.setAuthorName("Иван Петров");
        commentDto.setCreated(created);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Иван Петров");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(formatter));
    }

    @Test
    void shouldDeserializeCommentDto() throws Exception {
        String content = "{\"text\":\"Отличная вещь!\"}";

        CommentDto result = json.parse(content).getObject();

        assertThat(result.getId()).isNull();
        assertThat(result.getText()).isEqualTo("Отличная вещь!");
        assertThat(result.getAuthorName()).isNull();
        assertThat(result.getCreated()).isNull();
    }

    @Test
    void shouldSerializeCommentDtoWithoutOptionalFields() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличная вещь!");

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь!");
        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.authorName");
        assertThat(result).doesNotHaveJsonPathValue("$.created");
    }
}