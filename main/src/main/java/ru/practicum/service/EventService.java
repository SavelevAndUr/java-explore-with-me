package ru.practicum.service;

import ru.practicum.dto.*;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);
    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);
    EventFullDto getUserEvent(Long userId, Long eventId);
    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states,
                                      List<Long> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, Integer from,
                                        Integer size);

    EventFullDto getPublicEvent(Long id);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);
    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);
}