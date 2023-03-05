package ru.practicum.ewm.event.controller.prvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventControllerPrvt {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@Positive @PathVariable Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info(String.format("GET getEvents: userId = %d from = %d size = %d", userId, from, size));
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Positive @PathVariable Long userId, @Valid @RequestBody EventNewDto dto) {
        log.info(String.format("POST createEvent: userId = %d dto = %s", userId, dto.toString()));
        return eventService.createEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@Positive @PathVariable Long userId, @Positive @PathVariable Long eventId) {
        log.info(String.format("GET getEvent: userId = %d eventId = %d", userId, eventId));
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Positive @PathVariable Long userId, @Positive @PathVariable Long eventId,
                                    @Valid @RequestBody EventDtoUpdateUser dto) {
        log.info(String.format("PATCH updateEvent: userId = %d eventId = %d", userId, eventId));
        return eventService.updateEvent(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequests(@Positive @PathVariable Long userId, @Positive @PathVariable Long eventId) {
        log.info(String.format("GET getRequests: userId = %d eventId = %d", userId, eventId));
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestStatusUpdateResult updateRequestStatus(@Positive @PathVariable Long userId,
                                                         @Positive @PathVariable Long eventId,
                                                         @Valid @RequestBody RequestStatusUpdate dto) {
        log.info(String.format("PATCH updateRequestsStatus: userId = %d eventId = %d dto = %s",
                userId, eventId, dto.toString()));
        if (dto.getStatus() != RequestStatus.CONFIRMED && dto.getStatus() != RequestStatus.REJECTED) {
            throw new BadRequestException("Wrong status.");
        }

        return eventService.updateRequestsStatus(userId, eventId, dto);
    }

}