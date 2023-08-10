package ru.practicum.mapper;

import ru.practicum.dto.BackHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {


    public static BackHitDto toBackHitDto(Hit hit) {
        return new BackHitDto(
                hit.getApp(),
                hit.getUri(),
                0L
        );
    }

    public static Hit toDtoHit(HitDto hitDto) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Hit hit = new Hit();
        hit.setId(hitDto.getId());
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamps(LocalDateTime.parse(hitDto.getTimestamp(), formatter));
        return hit;
    }
}
