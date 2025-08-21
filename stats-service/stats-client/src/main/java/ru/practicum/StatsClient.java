package ru.practicum;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.HitDto;

@Component
public class StatsClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serverUrl = "http://stats-service:8080";

    public void sendHit(HitDto hit) {
        restTemplate.postForEntity(serverUrl + "/hit", hit, Void.class);
    }
}