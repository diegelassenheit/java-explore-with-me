package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.subscription.dto.SubscriptionDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.event.repository.EventRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ModelMapper modelMapper;
    private final UserService userService;


    public SubscriptionDto subscribeToUser(Long userId, Long personId) {
        User person = userService.getUserOrThrow(personId);

        if (!person.getAllowSubscription()) {
            throw new ForbiddenException(String.format("The user userId = %d has disabled subscriptions.", personId));
        }

        User user = userService.getUserOrThrow(userId);
        Subscription subscription = Subscription.builder().subscriber(user).person(person).build();

        return modelMapper.map(subscriptionRepository.save(subscription), SubscriptionDto.class);
    }


    public void unsubscribeFromUser(Long userId, Long subscriptionId) {
        User user = userService.getUserOrThrow(userId);
        Subscription subscription = getSubscriptionOrThrow(user, subscriptionId);
        subscriptionRepository.delete(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptions(Long userId, Integer from, Integer size) {
        User user = userService.getUserOrThrow(userId);

        return subscriptionRepository
                .getSubscriptionsBySubscriber(user, new Page(from, size))
                .stream().map(s -> modelMapper.map(s, SubscriptionDto.class)).collect(Collectors.toList());

    }

    public List<EventShortDto> getActualEvents(Long userId) {
        List<Event> events = eventRepository.getEventsBySubscription(userId, LocalDateTime.now());
        return eventMapper.toEventShortDtoList(events);
    }

    public Subscription getSubscriptionOrThrow(User user, Long subscriptionId) {
        return subscriptionRepository.getSubscriptionByIdAndSubscriber(subscriptionId, user).orElseThrow(
                () -> new NotFoundException(
                        String.format("Subscription for subscriptionId=%d and userId = %d not found.",
                                subscriptionId, user.getId())
                )
        );
    }

    public SubscriptionDto getSubscription(Long userId, Long subscriptionId) {
        return modelMapper.map(
                getSubscriptionOrThrow(userService.getUserOrThrow(userId), subscriptionId), SubscriptionDto.class
        );
    }


}