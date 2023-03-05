package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(Long userId) {
        return requestMapper.toDtoList(requestRepository.getRequestsByRequesterId(userId));
    }

    public RequestDto createRequest(Long userId, Long eventId) {

        checkUser(userId);
        checkEvent(eventId);

        User requester = userRepository.getReferenceById(userId);
        Event event = eventRepository.getReferenceById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User with id=" + userId + " is the owner of the event by id=" + eventId);
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Event by id=" + eventId + " was not published");
        }

        if (!event.addRequestAvailable()) {
            throw new ForbiddenException("Limit exhausted.");
        }

        Request newRequest = new Request();
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequester(requester);
        newRequest.setEvent(event);

        if (event.getRequestModeration()) {
            newRequest.setStatus(RequestStatus.PENDING);
        } else {
            newRequest.setStatus(RequestStatus.CONFIRMED);
            event.incConfirmedRequests();
            eventRepository.save(event);
        }

        return requestMapper.toDto(requestRepository.save(newRequest));
    }

    public RequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);

        Request request = requestRepository.getRequestByIdIsAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request by id=" + requestId + " and user id=" + userId + " was not found."));

        Event event = request.getEvent();
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            event.decConfirmedRequests();
            eventRepository.save(event);
        }

        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        return requestMapper.toDto(request);
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User by id=" + userId + " was not found.");
        }
    }

    private void checkEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event by id=" + eventId + " was not found.");
        }
    }
}