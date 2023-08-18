package ru.practicum.service;

import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.query.EventDynamicQueryDto;

import java.util.List;

public interface EventService {
    FullEventDto updateEvent(EventDto eventDto, Long eventId);
    List<FullEventDto> getEvent(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size);
    FullEventDto saveEvent(Long userId, EventDto eventDto);
    List<FullEventDto> getUserEvents(Long userId, Long from, Long size);
}
