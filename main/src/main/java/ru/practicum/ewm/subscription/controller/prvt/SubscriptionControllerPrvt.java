package ru.practicum.ewm.subscription.controller.prvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.subscription.dto.SubscriptionDto;
import ru.practicum.ewm.subscription.service.SubscriptionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionControllerPrvt {

    private final SubscriptionService subscriptionService;

    @GetMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionDto getSubscription(@Positive @PathVariable Long userId, @Positive @PathVariable Long subscriptionId) {
        log.info(String.format("GET getSubscription: userId = %d, subscriptionId = %d", userId, subscriptionId));
        return subscriptionService.getSubscription(userId, subscriptionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribeToUser(@Positive @PathVariable Long userId, @Positive @RequestParam Long personId) {
        log.info(String.format("POST subscribeToUser: userId = %d, personId = %d", userId, personId));
        return subscriptionService.subscribeToUser(userId, personId);
    }

    @DeleteMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribeFromUser(@Positive @PathVariable Long userId, @Positive @PathVariable Long subscriptionId) {
        log.info(String.format("DELETE unsubscribeFromUser: userId = %d, subscriptionId = %d", userId, subscriptionId));
        subscriptionService.unsubscribeFromUser(userId, subscriptionId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getSubscriptions(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info(String.format("GET getSubscriptions(): userId = %d, from = %d, size = %d", userId, from, size));
        return subscriptionService.getSubscriptions(userId, from, size);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getActualEvents(@Positive @PathVariable Long userId) {
        log.info(String.format("GET getActualEvents: userId = %d", userId));
        return subscriptionService.getActualEvents(userId);
    }
}