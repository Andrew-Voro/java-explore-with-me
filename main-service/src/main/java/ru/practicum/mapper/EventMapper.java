package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.Location;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static java.time.LocalDateTime.now;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {


    public static EventDto toEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory().getId(),
                event.getDescription(),
                event.getStateAction(),
                event.getEventDate().toString(),
                Location.builder().lat(event.getLat()).lon(event.getLon()).build(),
                event.getTitle(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getViews()

        );
    }


    public static FullEventDto toFullEventDto(Event event) {
        return new FullEventDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getInitiator(),
                Location.builder().lat(event.getLat()).lon(event.getLon()).build(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getStateAction(),
                event.getTitle(),
                event.getViews()
        );
    }
       /* Long id;
        String annotation;
        Long category;
        Long confirmedRequests;
        LocalDateTime createdOn;
        String description;
        LocalDateTime eventDate;
        Long initiator;
        Location location;
        Boolean paid;
        Long participantLimit;
        LocalDateTime publishedOn;
        Boolean requestModeration;
        String stateAction;
        String title;
        Long views;*/


    public static Event toDtoEvent(Long userId, EventDto eventDto, Category category) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new Event(
                eventDto.getId() != null ? eventDto.getId() : null,
                eventDto.getAnnotation() != null ? eventDto.getAnnotation() : null,
                category != null ? category : null,
                0L,
                now(),
                eventDto.getDescription() != null ? eventDto.getDescription() : null,
                eventDto.getEventDate() != null ? LocalDateTime.parse(eventDto.getEventDate(), formatter) : null,
                userId,
                eventDto.getLocation() != null && eventDto.getLocation().getLat() != null ? eventDto.getLocation().getLat() : null,
                eventDto.getLocation() != null && eventDto.getLocation().getLon() != null ? eventDto.getLocation().getLon() : null,
                eventDto.getPaid() != null ? eventDto.getPaid() : null,
                eventDto.getParticipantLimit() != null ? eventDto.getParticipantLimit() : null,
                now(),
                eventDto.getRequestModeration() != null ? eventDto.getRequestModeration() : null,
                eventDto.getStateAction() != null ? eventDto.getStateAction() : null,
                eventDto.getTitle() != null ? eventDto.getTitle() : null,
                new ArrayList<>(),
                eventDto.getViews() != null ? eventDto.getViews() : null
        );
    }
}



    /*Long id;
    String annotation;
    Long category;
    Long confirmedRequests;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    Long initiator;
    Float lat;
    Float lon;
    Boolean paid;
    Long participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    String stateAction;
    String title;*/

