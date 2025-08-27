package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.*;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Optional<Event> findById(Integer eventId);

    EventFullDto createEvent(Integer userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Integer userId, PageRequest page);

    Map<Integer, Integer> getStats(List<Event> events);

    EventFullDto getEventsById(Integer userId, Integer eventId);

    EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventDto updateEventUserDto);

    List<RequestDto> getRequestsByEventId(Integer userId, Integer eventId);

    RequestsByStatusDto updateEventRequestsStatus(Integer eventId, Integer userId, RequestStatusUpdateDto statusUpdateDto);

    List<EventFullDto> getEventsByAdminFilters(List<Integer> users, List<String> states, List<Integer> categories,
                                               String rangeStart, String rangeEnd, PageRequest page);

    List<EventShortDto> getEventsByPublicFilters(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                                 String rangeEnd, Boolean onlyAvailable, String sort, PageRequest of,
                                                 HttpServletRequest request);

    EventFullDto getEventById(Integer eventId, HttpServletRequest request);

    List<Event> findByIds(List<Integer> eventsId);
}