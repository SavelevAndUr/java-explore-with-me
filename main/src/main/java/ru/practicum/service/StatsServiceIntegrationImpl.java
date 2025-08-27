package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.ViewStatsRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceIntegrationImpl implements StatsServiceIntegration {

    private final StatsClient statsClient;

    @Override
    public void hit(String uri, String ip) {
        EndpointHit hit = EndpointHit.builder()
                .app("explore-with-me")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.hit(hit);
    }

    @Override
    public List<ViewStats> getStats(ViewStatsRequest request) {
        return statsClient.getStats(request);
    }

    @Override
    public Long getEventViews(Long eventId) {
        ViewStatsRequest request = ViewStatsRequest.builder()
                .app("explore-with-me")
                .start(LocalDateTime.now().minusYears(1))
                .end(LocalDateTime.now())
                .uris(List.of("/events/" + eventId))
                .unique(true)
                .build();

        List<ViewStats> stats = statsClient.getStats(request);
        return stats.isEmpty() ? 0L : stats.get(0).getHits();
    }
}