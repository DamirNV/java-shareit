package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void shouldFindAllByRequestorId() {
        User user = new User();
        user.setName("Пользователь");
        user.setEmail("user@example.com");
        entityManager.persist(user);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Ищу дрель");
        request1.setRequestor(user);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        entityManager.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Ищу молоток");
        request2.setRequestor(user);
        request2.setCreated(LocalDateTime.now());
        entityManager.persist(request2);

        entityManager.flush();

        List<ItemRequest> requests = requestRepository.findAllByRequestorId(
                user.getId(),
                Sort.by(Sort.Direction.DESC, "created")
        );

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Ищу молоток");
        assertThat(requests.get(1).getDescription()).isEqualTo("Ищу дрель");
    }

    @Test
    void shouldFindAllByRequestorIdNot() {
        User user1 = new User();
        user1.setName("Пользователь 1");
        user1.setEmail("user1@example.com");
        entityManager.persist(user1);

        User user2 = new User();
        user2.setName("Пользователь 2");
        user2.setEmail("user2@example.com");
        entityManager.persist(user2);

        ItemRequest request = new ItemRequest();
        request.setDescription("Ищу что-то");
        request.setRequestor(user1);
        request.setCreated(LocalDateTime.now());
        entityManager.persist(request);

        entityManager.flush();

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(
                user1.getId(),
                Sort.by(Sort.Direction.DESC, "created")
        );

        assertThat(requests).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoRequests() {
        User user = new User();
        user.setName("Пользователь");
        user.setEmail("user@example.com");
        entityManager.persist(user);
        entityManager.flush();

        List<ItemRequest> requests = requestRepository.findAllByRequestorId(
                user.getId(),
                Sort.by(Sort.Direction.DESC, "created")
        );

        assertThat(requests).isEmpty();
    }
}