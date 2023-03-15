package ru.practicum.ewm.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
public class UserDtoNew {

    @NotBlank
    private String name;

    @Email
    private String email;
    private Boolean allowSubscription = true;
}