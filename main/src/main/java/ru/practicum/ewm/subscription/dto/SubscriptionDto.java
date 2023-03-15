package ru.practicum.ewm.subscription.dto;

import lombok.*;
import ru.practicum.ewm.user.dto.UserDtoShort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {

    private Long id;

    private UserDtoShort subscriber;

    private UserDtoShort person;

}