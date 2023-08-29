package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private static final String API_PREFIX = "";

    @Autowired
    public StatsClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {  // http://localhost:9090
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(HitDto hit) {
        return post("/hit", hit);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) throws Exception {
        Map<String, Object> parameters = Map.of(
                "start", URLEncoder.encode(start, StandardCharsets.UTF_8.toString()),
                "end", URLEncoder.encode(end, StandardCharsets.UTF_8.toString()),
                "uris", (uris != null) ? uris.toString().subSequence(1, uris.get(0).length() + 1).toString() : "null",
                "unique", unique.toString()
        );

        return get("/stats" + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

}
