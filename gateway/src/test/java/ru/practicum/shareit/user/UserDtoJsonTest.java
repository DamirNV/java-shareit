package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Иван Петров");
        userDto.setEmail("ivan@example.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Иван Петров");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan@example.com");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"Иван Петров\",\"email\":\"ivan@example.com\"}";

        UserDto result = json.parse(content).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Иван Петров");
        assertThat(result.getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    void shouldDeserializeUserDtoWithoutId() throws Exception {
        String content = "{\"name\":\"Иван Петров\",\"email\":\"ivan@example.com\"}";

        UserDto result = json.parse(content).getObject();

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Иван Петров");
        assertThat(result.getEmail()).isEqualTo("ivan@example.com");
    }
}
