package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(Long userId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> requestIds); // Добавляем этот метод

    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    @Query("SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'")
    Long countConfirmedRequests(@Param("eventId") Long eventId);
}