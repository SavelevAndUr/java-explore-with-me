package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${ewm-stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .build()
        );
    }

    public ResponseEntity<List<ViewStats>> getStats(String start, String end, String uris, String unique) {
        if (Objects.nonNull(uris)) {
            Map<String, Object> parameters = Map.of(
                    "start", start,
                    "end", end,
                    "uris", uris,
                    "unique", unique
            );
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        } else {
            Map<String, Object> parameters = Map.of(
                    "start", start,
                    "end", end,
                    "unique", unique
            );
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }
    }

    public void createStats(EndpointHit endpointHit) {
        ResponseEntity<List<ViewStats>> response = post("/hit", endpointHit);
        if (Objects.isNull(response)) {
            ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Информация сохранена");
        }
    }
}