package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {

    @NotNull(message = "itemId не может быть null")
    private Long itemId;

    @NotNull(message = "start не может быть null")
    @FutureOrPresent(message = "start не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "end не может быть null")
    @Future(message = "end не может быть в прошлом")
    private LocalDateTime end;
}
