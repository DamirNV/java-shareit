package ru.practicum.shareit.booking;

public enum BookingState {
    ALL,        // все
    CURRENT,    // текущие
    PAST,       // прошедшие
    FUTURE,     // будущие
    WAITING,    // ожидающие подтверждения
    REJECTED    // отклонённые
}
