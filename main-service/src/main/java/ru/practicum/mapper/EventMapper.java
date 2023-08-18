package ru.practicum.mapper;

import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.Location;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;

public class EventMapper {


    /*public static EventDto toEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                event.getDescription(),
                event.getEventDate(),
                Location.builder().lat(event.getLat()).lon(event.getLon()).build(),
                event.getTitle(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration())

        );
    }*/


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
                0L
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


    public static Event toDtoEvent(Long userId, EventDto eventDto,Category category) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new Event(
                eventDto.getId(),
                eventDto.getAnnotation(),
                category,
                0L,
                now(),
                eventDto.getDescription(),
                LocalDateTime.parse(eventDto.getEventDate(), formatter),
                userId,
                eventDto.getLocation().getLat(),
                eventDto.getLocation().getLon(),
                eventDto.getPaid(),
                eventDto.getParticipantLimit(),
                now(),
                eventDto.getRequestModeration(),
                eventDto.getStateAction(),
                eventDto.getTitle()
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

