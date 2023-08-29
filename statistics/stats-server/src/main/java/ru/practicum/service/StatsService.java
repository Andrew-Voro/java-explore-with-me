package ru.practicum.service;

import ru.practicum.dto.BackHitDto;
import ru.practicum.dto.HitDto;

import java.util.List;

public interface StatsService {
    BackHitDto saveHit(HitDto hit);

    List<BackHitDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
