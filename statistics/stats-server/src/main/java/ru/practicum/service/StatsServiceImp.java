package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.BackHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImp implements StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public BackHitDto saveHit(HitDto hit) {
        // Long numberOfHits = (long) statsRepository.findByAppAndUri(hit.getApp(), hit.getUri()).size();
        Hit saveHit = statsRepository.save(HitMapper.toDtoHit(hit));
        BackHitDto backHitDto = HitMapper.toBackHitDto(saveHit);
        //backHitDto.setHits(numberOfHits);
        return backHitDto;
    }

    @Transactional
    @Override
    public List<BackHitDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        List<BackHitDto> stats = new ArrayList<>();
        if (uris != null && uris.get(0).equals("null")) {
            uris = null;
        }
        if (uris != null) {
            for (String uri : uris) {
                List<Hit> s = statsRepository.findByUriAndTimestampsIsAfterAndTimestampsIsBefore(uri, startTime, endTime);
                int sSize = s.size();
                Hit h = s.get(sSize - 1);
                BackHitDto newBackHitDto = BackHitDto.builder().uri(h.getUri()).app(h.getApp()).hits((long) sSize).build();
                stats.add(newBackHitDto);
            }
        } else {
            List<Hit> s = statsRepository.findByTimestampsIsAfterAndTimestampsIsBefore(startTime, endTime);
            for (Hit hit : s) {
                BackHitDto newBackHitDto = BackHitDto.builder().uri(hit.getUri()).app(hit.getApp()).hits(1L).build();
                stats.add(newBackHitDto);
            }
        }

        if (unique != null && unique == true) {
            HashSet<BackHitDto> setStats = new HashSet<>();
            setStats.addAll(stats);
            List<BackHitDto> listStats = new ArrayList<>();
            listStats.addAll(setStats);
            for (BackHitDto s : listStats) {
                s.setHits(1L);
            }
            return listStats;
        }

        List<BackHitDto> sortedStats = stats.stream().sorted((x, y) -> (y.getHits().compareTo(x.getHits()))).collect(Collectors.toList());
        return sortedStats;
    }
}
