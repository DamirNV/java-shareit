package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("Пользователь 1");
        user1.setEmail("user1@example.com");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("Пользователь 2");
        user2.setEmail("user2@example.com");
        entityManager.persist(user2);

        request1 = new ItemRequest();
        request1.setDescription("Ищу дрель");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        entityManager.persist(request1);

        request2 = new ItemRequest();
        request2.setDescription("Ищу молоток");
        request2.setRequestor(user1);
        request2.setCreated(LocalDateTime.now());
        entityManager.persist(request2);

        entityManager.flush();
    }

    @Test
    void shouldFindAllByRequestorId() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(
                user1.getId(),
                Sort.by(Sort.Direction.DESC, "created")
        );

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Ищу молоток");
        assertThat(requests.get(1).getDescription()).isEqualTo("Ищу дрель");
    }

    @Test
    void shouldFindAllByRequestorIdNot() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(
                user1.getId(),
                Sort.by(Sort.Direction.DESC, "created")
        );

        assertThat(requests).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoRequests() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(
                999L,
                Sort.by(Sort.Direction.DESC, "created")
        );

        assertThat(requests).isEmpty();
    }
}