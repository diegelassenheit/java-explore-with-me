package ru.practicum.ewm.subscription.repository;

import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> getSubscriptionsBySubscriber(User subscriber, Pageable page);

    Optional<Subscription> getSubscriptionByIdAndSubscriber(Long id, User subscriber);


}