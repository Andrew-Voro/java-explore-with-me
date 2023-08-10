package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.BackHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<BackHitDto> saveHit(@RequestBody HitDto hit) {

        log.info("Посещение  зафиксировано.");

        return new ResponseEntity<>(statsService.saveHit(hit), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<BackHitDto>> getStats(@RequestParam(name = "start") String start,
                                                     @RequestParam(name = "end") String end,
                                                     @RequestParam(name = "uris",required = false) List<String> uris,
                                                     @RequestParam(name = "unique",required = false, defaultValue = "false") Boolean unique) throws Exception{
        String startStr = URLDecoder.decode(start, StandardCharsets.UTF_8.toString());
        String endStr = URLDecoder.decode(end, StandardCharsets.UTF_8.toString());

        return new ResponseEntity<>(statsService.getStats(startStr, endStr,  uris, unique), HttpStatus.OK);
    }

}
