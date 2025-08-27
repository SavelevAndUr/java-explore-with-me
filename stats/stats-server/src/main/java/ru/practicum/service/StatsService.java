package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface StatsService {
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);

    EndpointHit createStats(EndpointHit endpointHit);
}