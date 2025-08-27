package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventDto;
import ru.practicum.service.EventService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByFilters(@RequestParam(required = false) List<Integer> users,
                                                 @RequestParam(required = false) List<String> states,
                                                 @RequestParam(required = false) List<Integer> categories,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting events by following filters userIds={}, states={}, categories={}, rangeStart={}, " +
                "rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByAdminFilters(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from, size));
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto updateEvent(@PathVariable Integer eventId,
                                    @Valid @RequestBody UpdateEventDto updateEventAdminDto) {
        log.info("Updating event={}  by following data = {}", eventId, updateEventAdminDto);
        return eventService.updateEvent(null, eventId, updateEventAdminDto);
    }
}