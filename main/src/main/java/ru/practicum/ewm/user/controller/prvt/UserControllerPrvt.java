package ru.practicum.ewm.user.controller.prvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoUpdate;


import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Validated
public class UserControllerPrvt {

    private final UserService userService;

    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUserPrivacy(@Positive @PathVariable Long userId, @Valid @RequestBody UserDtoUpdate dto) {
        log.info(String.format("PATCH updateUserPrivacy: userId = %d, dto = %s", userId, dto.toString()));
        return userService.update(userId, dto);
    }
}