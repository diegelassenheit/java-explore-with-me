package ru.practicum.ewm.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.controller.pub.EventSort;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.HitDtoRequest;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.EwmUtils;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<EventState> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size) {

        QPredicates predicates = QPredicates.builder().add(users, QEvent.event.initiator.id::in)
                .add(states, QEvent.event.state::in)
                .add(categories, QEvent.event.category.id::in)
                .add(rangeStart, QEvent.event.eventDate::goe)
                .add(rangeEnd, QEvent.event.eventDate::loe);

        Page page = new Page(from, size);
        Predicate predicate = predicates.buildAnd();

        return eventMapper.toEventFullDtoList(
                (predicate == null)
                        ? eventRepository.findAll(page).toList()
                        : eventRepository.findAll(predicate, page).toList()
        );
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, Optional<EventSort> sortOptional,
                                         Integer from, Integer size, HttpServletRequest request) {

        generateStatEvent(request);

        QPredicates predicatesOr = QPredicates.builder().add(text, QEvent.event.description::containsIgnoreCase)
                .add(text, QEvent.event.annotation::containsIgnoreCase);

        QPredicates predicatesAnd = QPredicates.builder().add(categories, QEvent.event.category.id::in)
                .add(paid, QEvent.event.paid::eq)
                .add(rangeStart, QEvent.event.eventDate::goe)
                .add(rangeEnd, QEvent.event.eventDate::loe)
                .add(onlyAvailable, aBoolean -> {
                    if (aBoolean) {
                        return QEvent.event.confirmedRequests.loe(QEvent.event.participantLimit);
                    }
                    return Expressions.asBoolean(true).isTrue();
                })
                .add(EventState.PUBLISHED, QEvent.event.state::eq);

        if (rangeStart == null && rangeEnd == null) {
            predicatesAnd.add(LocalDateTime.now(), QEvent.event.eventDate::gt);
        }

        List<Predicate> predicatesAll = List.of(predicatesOr.buildOr(), predicatesAnd.buildAnd());
        Predicate predicate = ExpressionUtils.allOf(predicatesAll);

        Sort sort = Sort.unsorted();

        if (sortOptional.isPresent()) {
            switch (sortOptional.get()) {
                case EVENT_DATE:
                    sort = Sort.by("eventDate");
                    break;
                case VIEWS:
                    sort = Sort.by("views");
                    break;
            }
        }

        Page page = new Page(from, size, sort);

        return eventMapper.toEventShortDtoList(
                (predicate == null)
                        ? eventRepository.findAll(page).toList()
                        : eventRepository.findAll(predicate, page).toList()
        );

    }

    public EventFullDto updateEvent(Long eventId, EventDtoUpdateAdmin dto) {
        validateEventTime(dto.getEventDate(), 1);
        checkEvent(eventId);

        Event entityTarget = eventRepository.getReferenceById(eventId);
        Event srcEvent = eventMapper.toEventModel(dto);

        EwmUtils.copyNotNullProperties(srcEvent, entityTarget);
        doEventAction(entityTarget, dto.getStateAction());

        return eventMapper.toEventFullDto(eventRepository.save(entityTarget));
    }

    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        generateStatEvent(request);
        checkEvent(id);

        return eventMapper.toEventFullDto(eventRepository.getEventByIdAndState(id, EventState.PUBLISHED));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        validateUser(userId);

        return eventMapper.toEventShortDtoList(
                eventRepository.getEventByInitiatorId(userId, new Page(from, size))
        );
    }

    public EventFullDto createEvent(Long userId, EventNewDto dto) {
        validateEventTime(dto.getEventDate(), 2);
        validateUser(userId);

        Event event = eventMapper.toEventModel(dto);

        event.setInitiator(userRepository.getReferenceById(userId));
        event.setState(EventState.PENDING);
        event.setViews(0);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {
        Optional<Event> optionalEvent = eventRepository.getEventByIdAndInitiatorId(eventId, userId);

        return eventMapper.toEventFullDto(optionalEvent.orElseThrow(
                () -> new NotFoundException("Event by id=" + eventId + " and userId=" + userId + " was not found."))
        );
    }

    public EventFullDto updateEvent(Long userId, Long eventId, EventDtoUpdateUser dto) {

        if (dto.getEventDate() != null) {
            validateEventTime(dto.getEventDate(), 2);
        }

        Optional<Event> optionalEvent = eventRepository.getEventByIdAndInitiatorId(eventId, userId);
        Event eventTarget = optionalEvent.orElseThrow(() -> new NotFoundException(
                String.format("Event with eventId=%d and userId=%d wasn't found", eventId, userId)));

        if (eventTarget.getState() == EventState.PUBLISHED) {
            throw new ForbiddenException(String.format("Event with eventId = %d already has been published", eventId));
        }

        Event src = eventMapper.toEventModel(dto);
        EwmUtils.copyNotNullProperties(src, eventTarget);

        doEventAction(eventTarget, dto.getStateAction());

        return eventMapper.toEventFullDto(eventRepository.save(eventTarget));
    }

    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(Long userId, Long eventId) {
        return requestMapper.toDtoList(requestRepository.getRequestsByEventIdAndEventInitiatorId(eventId, userId));
    }

    public RequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId, RequestStatusUpdate dto) {
        validateUser(userId);
        checkEvent(eventId);

        Event event = eventRepository.getEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with eventId=%d and userId=%d wasn't found", eventId, userId)));

        RequestStatusUpdateResult requestStatusUpdateResult = new RequestStatusUpdateResult();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return requestStatusUpdateResult;
        }

        List<Request> requests = requestRepository.getRequestsByEventIdAndEventInitiatorIdAndIdIn(
                eventId, userId, dto.getRequestIds()
        );

        requests.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ForbiddenException(String.format("Request with id = %d is not PENDING", request.getId()));
            }

            request.setStatus(dto.getStatus());

            if (dto.getStatus() == RequestStatus.CONFIRMED) {
                event.incConfirmedRequests();

                if (event.limitExhausted()) {
                    throw new ForbiddenException("Request limit exceeded");
                }
            }
        });

        requestRepository.saveAll(requests);
        eventRepository.save(event);

        if (dto.getStatus().equals(RequestStatus.CONFIRMED)) {
            requestStatusUpdateResult.setConfirmedRequests(
                    requestMapper.toDtoList(requestRepository.getRequestsByEventIdAndEventInitiatorIdAndStatus(
                            eventId, userId, RequestStatus.CONFIRMED))
            );
        } else if (dto.getStatus().equals(RequestStatus.REJECTED)) {
            requestStatusUpdateResult.setRejectedRequests(
                    requestMapper.toDtoList(requestRepository.getRequestsByEventIdAndEventInitiatorIdAndStatus(
                            eventId, userId, RequestStatus.REJECTED))
            );
        }

        return requestStatusUpdateResult;
    }

    private void doEventAction(Event entityTarget, EventStateAction stateAction) {
        if (stateAction == EventStateAction.PUBLISH_EVENT) {
            if (entityTarget.getState() == EventState.PUBLISHED) {
                throw new ForbiddenException("Event has been published already.");
            } else if (entityTarget.getState() == EventState.CANCELED) {
                throw new ForbiddenException("Event is canceled.");
            }
            entityTarget.setState(EventState.PUBLISHED);
        } else if (stateAction == EventStateAction.REJECT_EVENT) {
            if (entityTarget.getState() == EventState.PUBLISHED) {
                throw new ForbiddenException("Event has been published already.");
            }
            entityTarget.setState(EventState.CANCELED);
        } else if (stateAction == EventStateAction.CANCEL_REVIEW) {
            entityTarget.setState(EventState.CANCELED);
        } else if (stateAction == EventStateAction.SEND_TO_REVIEW) {
            entityTarget.setState(EventState.PENDING);
        }

    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with userId = %d is not found", userId));
        }
    }

    private void checkEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with eventId = %d is not found", eventId));
        }
    }

    private void validateEventTime(LocalDateTime dateTime, int hours) {
        if (dateTime == null) {
            return;
        }

        LocalDateTime dtNow = LocalDateTime.now();
        if (dtNow.plusHours(hours).isAfter(dateTime)) {
            throw new ForbiddenException("The date should happen in future");
        }
    }

    private void generateStatEvent(HttpServletRequest request) {
        HitDtoRequest dto = new HitDtoRequest();

        dto.setApp("ewm-main-service");
        dto.setIp(request.getRemoteAddr());
        dto.setTimestamp(LocalDateTime.now());
        dto.setUri(request.getRequestURI());

        try {
            ResponseEntity<Object> result = statsClient.createHit(dto);

            if (result.getStatusCode() == HttpStatus.CREATED) {
                log.info("SUCCESS Created hit = {} status={}", dto, result.getStatusCode());
            } else {
                log.info("FAILED to create hit={} status={}", dto, result.getStatusCode());
            }
        } catch (RuntimeException ex) {
            log.info("Hit creation error: " + ex.getMessage());
        }
    }
}