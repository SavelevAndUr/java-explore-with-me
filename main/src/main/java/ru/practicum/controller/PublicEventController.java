package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.EventService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {
    private final EventService eventService;

    @GetMapping(value = "/{eventId}")
    public EventFullDto getEventById(@PathVariable Integer eventId, HttpServletRequest request) {
        log.info("Getting events by id={}", eventId);
        return eventService.getEventById(eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEventsByFilters(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Integer> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) String rangeStart,
                                                  @RequestParam(required = false) String rangeEnd,
                                                  @RequestParam(required = false, defaultValue = "false")
                                                  Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size,
                                                  HttpServletRequest request) {
        log.info("Getting events by following filters text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEventsByPublicFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                PageRequest.of(from, size), request);
    }
}