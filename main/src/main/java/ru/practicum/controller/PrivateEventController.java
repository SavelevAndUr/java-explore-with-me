package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Integer userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Creating event={} from user={}", newEventDto, userId);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Integer userId,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting events for user={} from={} size={}", userId, from, size);
        return eventService.getEventsByUserId(userId, PageRequest.of(from, size));
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("Getting event={} fo user={}", eventId, userId);
        return eventService.getEventsById(userId, eventId);
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto updateEvent(@PathVariable Integer userId, @PathVariable Integer eventId,
                                    @Valid @RequestBody UpdateEventDto updateEventUserDto) {
        log.info("Updating event={} from user={} by following data = {}", eventId, userId, updateEventUserDto);
        return eventService.updateEvent(userId, eventId, updateEventUserDto);
    }

    @GetMapping(value = "/{eventId}/requests")
    public List<RequestDto> getRequestsByEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("Getting requests for event={} fo user={}", eventId, userId);
        return eventService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping(value = "/{eventId}/requests")
    public RequestsByStatusDto updateStatusRequests(@PathVariable Integer userId, @PathVariable Integer eventId,
                                                    @Valid @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        log.info("Updating status for requests={} for event={} from user={}", requestStatusUpdateDto, eventId, userId);
        return eventService.updateEventRequestsStatus(eventId, userId, requestStatusUpdateDto);
    }
}