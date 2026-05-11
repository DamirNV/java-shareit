package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Мощная дрель 600Вт");
        item1.setAvailable(true);
        item1.setOwner(owner.getId());
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("Молоток");
        item2.setDescription("Тяжелый молоток");
        item2.setAvailable(true);
        item2.setOwner(owner.getId());
        entityManager.persist(item2);

        entityManager.flush();
    }

    @Test
    void shouldFindAllByOwner() {
        List<Item> items = itemRepository.findAllByOwner(owner.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getName).contains("Дрель", "Молоток");
    }

    @Test
    void shouldSearchByText() {
        List<Item> items = itemRepository.search("дрель");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void shouldSearchByTextCaseInsensitive() {
        List<Item> items = itemRepository.search("ДРЕЛЬ");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void shouldSearchByDescription() {
        List<Item> items = itemRepository.search("мощная");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void shouldReturnAllItemsWhenTextIsEmpty() {
        List<Item> items = itemRepository.search("");

        assertThat(items).hasSize(2);  // ожидаем 2 вещи
        assertThat(items).extracting(Item::getName).contains("Дрель", "Молоток");
    }

    @Test
    void shouldNotReturnUnavailableItemsInSearch() {
        item1.setAvailable(false);
        entityManager.persist(item1);
        entityManager.flush();

        List<Item> items = itemRepository.search("дрель");

        assertThat(items).isEmpty();
    }
}