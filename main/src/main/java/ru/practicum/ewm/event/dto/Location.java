package ru.practicum.ewm.event.dto;

import javax.persistence.Embeddable;
import lombok.Setter;
import lombok.Getter;

@Embeddable
@Getter
@Setter
public class Location {
    // не знаю, что будут делать авторы postman-тестов с сэкономленными на lat/lon буквами.
    // Наверное, где-то есть банк, куда можно сдать -titude/-gitude буквы под проценты. -_-
    private Float lat;

    private Float lon;
}