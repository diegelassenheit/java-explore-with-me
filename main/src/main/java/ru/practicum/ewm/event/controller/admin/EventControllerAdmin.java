package ru.practicum.ewm.event.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDtoUpdateAdmin;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.util.DateFormatConstant;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class EventControllerAdmin {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @DateTimeFormat(pattern = DateFormatConstant.TIME_PATTERN) @RequestParam(required = false) LocalDateTime rangeStart,
                                        @DateTimeFormat(pattern = DateFormatConstant.TIME_PATTERN) @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        // и вот сейчас я нагуглил, что оказывается, log умеет в такое же простое форматирование, как .format() в питоне
        // и выходит, я напрасно везде писал String.fmt. Для списков так сделаю, а в остальном буду по-старинке
        log.info("GET getEvents: users = {} states = {} categories = {} rangeStart = {} rangeEnd = {}" +
                        " from = {} size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Positive @PathVariable Long eventId, @RequestBody EventDtoUpdateAdmin dto) {
        log.info(String.format("PATCH updateEvent: eventId = %d dto = %s", eventId, dto.toString()));
        return eventService.updateEvent(eventId, dto);
    }
}
