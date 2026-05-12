package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {
        User user = new User();
        user.setName("Тест Пользователь");
        user.setEmail("test@example.com");

        User saved = userRepository.save(user);
        entityManager.flush();

        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Тест Пользователь");
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldCheckEmailExists() {
        User user = new User();
        user.setName("Пользователь");
        user.setEmail("exists@example.com");
        userRepository.save(user);
        entityManager.flush();

        boolean exists = userRepository.existsByEmail("exists@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailNotExists() {
        boolean exists = userRepository.existsByEmail("notexists@example.com");

        assertThat(exists).isFalse();
    }
}