package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.*;
import ru.practicum.model.Event;
import ru.practicum.model.Rate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Optional<Event> findById(Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Long userId, PageRequest page);

    Map<Long, Integer> getStats(List<Event> events);

    EventFullDto getEventsById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEventUserDto);

    List<RequestDto> getRequestsByEventId(Long userId, Long eventId);

    RequestsByStatusDto updateEventRequestsStatus(Long eventId, Long userId, RequestStatusUpdateDto statusUpdateDto);

    List<EventFullDto> getEventsByAdminFilters(List<Long> users, List<String> states, List<Integer> categories,
                                               String rangeStart, String rangeEnd, PageRequest page);

    List<EventShortDto> getEventsByPublicFilters(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                                 String rangeEnd, Boolean onlyAvailable, String sort, PageRequest of,
                                                 HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    List<Event> findByIds(List<Long> eventsId);

    EventShortDto addRateToEvent(Long userId, Long eventId, Rate rate);

    EventShortDto deleteRateFromEvent(Long userId, Long eventId);
}