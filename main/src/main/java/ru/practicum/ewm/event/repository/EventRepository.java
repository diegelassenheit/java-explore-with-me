package ru.practicum.ewm.event.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.dto.EventState;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Event getEventByIdAndState(Long id, EventState state);

    Optional<Event> getEventByIdAndInitiatorId(Long id, Long initiatorId);

    List<Event> getEventByInitiatorId(Long initiatorId, Page page);

    Set<Event> getEventsByIdIn(Collection<Long> id);

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id IN (SELECT s.person.id FROM Subscription s WHERE s.subscriber.id = ?1 AND s.person.allowSubscription = true ) " +
            "AND e.state = ru.practicum.ewm.event.dto.EventState.PUBLISHED AND e.eventDate >= ?2")
    List<Event> getEventsBySubscription(Long userId, LocalDateTime onDateTime);

}