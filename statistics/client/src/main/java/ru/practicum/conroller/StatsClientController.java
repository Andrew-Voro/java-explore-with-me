package ru.practicum.conroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitDto;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class StatsClientController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody HitDto hit) {

        log.info("Посещение  зафиксировано.");

        return statsClient.saveHit(hit);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") String start,
                                           @RequestParam(name = "end") String end,
                                           @RequestParam(name = "uris", required = false) List<String> uris,
                                           @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) throws Exception {

        return statsClient.getStats(start, end, uris, unique);
    }

}
