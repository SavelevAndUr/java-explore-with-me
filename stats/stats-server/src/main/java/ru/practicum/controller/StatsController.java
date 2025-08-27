package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatsService;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStats> get(@RequestParam(name = "start") String start,
                               @RequestParam(name = "end") String end,
                               @RequestParam(name = "uris", required = false) List<String> uris,
                               @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Getting stats from {} to {}, uris = {}, unique = {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit create(@RequestBody EndpointHit endpointHit) {
        log.info("Creating stats {}", endpointHit);
        return statsService.createStats(endpointHit);
    }
}