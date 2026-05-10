package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.UserShortDto;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemShortDto(booking.getItem().getId(), booking.getItem().getName()),
                new UserShortDto(booking.getBooker().getId(), booking.getBooker().getName()),
                booking.getStatus()
        );
    }
}