package ru.practicum.ewm.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class UserDto {

    private Long id;

    private String email;

    private String name;
    private Boolean allowSubscription;
}