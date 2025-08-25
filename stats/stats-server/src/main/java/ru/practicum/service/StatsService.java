package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.repository.StatRecord;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void create(EndpointHit hit) {
        StatRecord record = StatMapper.toStatRecord(hit);
        statsRepository.save(record);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, dtf);
        LocalDateTime endDate = LocalDateTime.parse(end, dtf);
        if (unique) {
            return statsRepository.searchUniqueViewStatsWithUris(startDate, endDate, uris);
        } else {
            return statsRepository.searchViewStatsWithUris(startDate, endDate, uris);
        }
    }
}
