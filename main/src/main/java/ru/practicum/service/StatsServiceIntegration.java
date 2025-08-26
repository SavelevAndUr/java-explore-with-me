package ru.practicum.service;

import ru.practicum.dto.ViewStats;
import ru.practicum.dto.ViewStatsRequest;

import java.util.List;

public interface StatsServiceIntegration {
    void hit(String uri, String ip);
    List<ViewStats> getStats(ViewStatsRequest request);
    Long getEventViews(Long eventId);
}