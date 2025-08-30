package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.InitiatorRatingDto;
import ru.practicum.dto.RatingDto;
import ru.practicum.model.Rate;
import ru.practicum.service.RatingService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping(value = "/events")
    public List<RatingDto> getRatingsForEvents(@RequestParam(required = false, defaultValue = "LIKE") Rate rate,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting rating for events by rate={}, from={}, size={}", rate, from, size);
        return ratingService.getRatingsForEvents(rate, PageRequest.of(from, size));
    }

    @GetMapping(value = "/users")
    public List<InitiatorRatingDto> getRatingsForInitiators(@RequestParam(required = false, defaultValue = "LIKE")
                                                                Rate rate,
                                                            @RequestParam(required = false, defaultValue = "0")
                                                            int from,
                                                            @RequestParam(required = false, defaultValue = "10")
                                                            int size) {
        log.info("Getting rating for initiators by rate={}, from={}, size={}", rate, from, size);
        return ratingService.getRatingsForInitiators(rate, PageRequest.of(from, size));
    }
}