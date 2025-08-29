package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    @Query("select r from Request r where r.status = :status and r.event = :event")
    List<Request> getRequestByStatusIs(RequestStatus status, Event event);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}