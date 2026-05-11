package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@example.com");
        entityManager.persist(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        entityManager.persist(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        entityManager.flush();
    }

    @Test
    void shouldFindByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId(), Sort.by(Sort.Direction.DESC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void shouldFindByBookerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldReturnEmptyWhenStatusNotFound() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"));

        assertThat(bookings).isEmpty();
    }

    @Test
    void shouldFindAllByOwner() {
        List<Booking> bookings = bookingRepository.findAllByOwner(owner.getId(), Sort.by(Sort.Direction.DESC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getItem().getOwner()).isEqualTo(owner.getId());
    }
}