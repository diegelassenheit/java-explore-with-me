package ru.practicum.ewm.event.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.util.DateFormatConstant;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventControllerPublic {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @DateTimeFormat(pattern = DateFormatConstant.TIME_PATTERN) @RequestParam(required = false) LocalDateTime rangeStart,
                                         @DateTimeFormat(pattern = DateFormatConstant.TIME_PATTERN) @RequestParam(required = false) LocalDateTime rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) Optional<EventSort> sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {

        log.info("GET getEvents: text = {} categories = {} paid = {} rangeStart = {} " +
                        "rangeEnd = {} onlyAvailable = {} sort = {} from = {} size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@Positive @PathVariable Long id, HttpServletRequest request) {
        log.info(String.format("GET getEvent: id=%d", id));
        return eventService.getEvent(id, request);
    }

}