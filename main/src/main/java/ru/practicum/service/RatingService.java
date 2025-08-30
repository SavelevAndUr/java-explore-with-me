package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.InitiatorRatingDto;
import ru.practicum.dto.RatingDto;
import ru.practicum.model.Event;
import ru.practicum.model.Rate;
import ru.practicum.model.User;

import java.util.List;
import java.util.Map;

public interface RatingService {
    RatingDto addRate(User rater, Event event, Rate rate);

    RatingDto deleteRate(User rater, Event event);

    RatingDto getRatingByEvent(Event event);

    Map<Long, RatingDto> getRatingsByEvents(List<Event> events);

    List<RatingDto> getRatingsForEvents(Rate rate, PageRequest page);

    List<InitiatorRatingDto> getRatingsForInitiators(Rate rate, PageRequest of);
}
