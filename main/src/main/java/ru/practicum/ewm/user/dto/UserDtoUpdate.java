package ru.practicum.ewm.user.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;


@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class UserDtoUpdate {

    private Boolean allowSubscription;

}