package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.enums.State;
import ru.practicum.handler.exception.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CustomEventRepository;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CustomEventRepository customEventRepository;
    private final CategoryRepository categoryRepository;

    @Transactional ///
    @Override
    public FullEventDto updateEvent(EventDto eventDto, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));


        /*if (!(event.getStateAction().equals(State.PUBLISHED)||event.getStateAction().equals(State.PUBLISH_EVENT)
        ||eventDto.getStateAction().equals(State.PUBLISHED)||eventDto.getStateAction().equals(State.PUBLISH_EVENT))) {
            throw new ConflictException("Status not Published");
        }*/
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getCategory() != null) {

            event.setCategory(categoryRepository.findById(eventDto.getCategory()).get());
        }
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), formatter);
            event.setEventDate(eventDate);
        }
        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (event.getStateAction() != null) {
            if (eventDto.getStateAction().equals(State.PUBLISH_EVENT)) {
                eventDto.setStateAction(State.PUBLISHED);
            }
            event.setStateAction(eventDto.getStateAction());
        }
        return EventMapper.toFullEventDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public List<FullEventDto> getEvent(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return customEventRepository.findEvent(eventDynamicQueryDto, page).stream().map(EventMapper::toFullEventDto).collect(Collectors.toList());
    }


    @Transactional
    public FullEventDto saveEvent(Long userId, EventDto eventDto) {

        Event event = eventRepository.save(EventMapper.toDtoEvent(userId, eventDto, categoryRepository.findById(eventDto.getCategory()).get()));
        return EventMapper.toFullEventDto(event);
    }

    @Transactional
    public List<FullEventDto> getUserEvents(Long userId, Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return eventRepository.findByInitiator(userId, page).stream().map(EventMapper::toFullEventDto).collect(Collectors.toList());
    }
}
