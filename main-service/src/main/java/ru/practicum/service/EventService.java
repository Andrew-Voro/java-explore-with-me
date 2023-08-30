package ru.practicum.service;

import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.query.EventDynamicQueryDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    FullEventDto updateEvent(EventDto eventDto, Long eventId);

    List<FullEventDto> getEvents(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size, HttpServletRequest request) throws Exception;

    List<FullEventDto> getEventByAdmin(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size);

    FullEventDto saveEvent(Long userId, EventDto eventDto);

    List<FullEventDto> getUserEvents(Long userId, Long from, Long size);

    FullEventDto getEventById(Long id, HttpServletRequest request) throws Exception;

    FullEventDto getUserEventById(Long userId, Long eventId);

    FullEventDto updateUserEvent(EventDto eventDto, Long userId, Long eventId);
}
