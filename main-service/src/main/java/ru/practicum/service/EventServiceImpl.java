package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.constant.CommonConstants;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.enums.State;
import ru.practicum.handler.exception.ConflictException;
import ru.practicum.handler.exception.ObjectNotFoundException;
import ru.practicum.handler.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CustomEventRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CustomEventRepository customEventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Transactional ///
    @Override
    public FullEventDto updateEvent(EventDto eventDto, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));

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

            LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), CommonConstants.formatter);
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Нельзя менять дату события на уже наступившую");
            }
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
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(State.PUBLISH_EVENT)) {
                if (event.getStateAction().equals(State.PUBLISHED)) {
                    throw new ConflictException("Событие уже опубликовано");
                } else if (event.getStateAction().equals(State.REJECTED)) {
                    throw new ConflictException("Событие отменено и не может быть опубликованно");
                } else {
                    event.setStateAction(State.PUBLISHED);
                }
            } else if (eventDto.getStateAction().equals(State.REJECT_EVENT)) {
                if (event.getStateAction().equals(State.PUBLISHED)) {
                    throw new ConflictException("Нельзя отменить опубликованное событие");
                } else {
                    event.setStateAction(State.REJECTED);
                }
            } else {
                event.setStateAction(eventDto.getStateAction());
            }
        }


        return EventMapper.toFullEventDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public FullEventDto updateUserEvent(EventDto eventDto, Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Изменить событие может только его инициатор и администратор");
        }
        if (event.getStateAction().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменить опубликованное событие может только его администратор");
        }

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
            LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), CommonConstants.formatter);
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Нельзя менять дату события на уже наступившую");
            }
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
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(State.CANCEL_REVIEW)) {
                event.setStateAction(State.CANCELED);
            } else if (eventDto.getStateAction().equals(State.SEND_TO_REVIEW)) {
                event.setStateAction(State.PENDING);
            } else {
                event.setStateAction(eventDto.getStateAction());
            }
        }

        return EventMapper.toFullEventDto(eventRepository.save(event));

    }


    @Transactional
    @Override
    public List<FullEventDto> getEventByAdmin(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size) {
        return customEventRepository.findEventByAdmin(eventDynamicQueryDto, from, size).stream().map(EventMapper::toFullEventDto).collect(Collectors.toList());
    }


    @Transactional
    public FullEventDto saveEvent(Long userId, EventDto eventDto) {

        Event event = eventRepository.save(EventMapper.toDtoEvent(userId, eventDto, categoryRepository.findById(eventDto.getCategory()).get()));
        return EventMapper.toFullEventDto(event);
    }


    @Transactional
    public FullEventDto getEventById(Long id, HttpServletRequest request) throws Exception {
        Event event = eventRepository.findByIdAndStateAction(id, State.PUBLISHED).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        event.setViews(event.getViews() != null ? event.getViews() : 0L);
        ResponseEntity<Object> hit = statsClient.saveHit(HitDto.builder().app("ewm-main-service").uri(request.getRequestURI())
                .ip(request.getRemoteAddr()).timestamp(LocalDateTime.now().format(formatter)).build());

        String start = LocalDateTime.now().minusYears(10L).format(formatter);
        String end = LocalDateTime.now().plusYears(10L).format(formatter);

        List<String> uri = new ArrayList<>();
        uri.add(request.getRequestURI());

        String body = statsClient.getStats(start, end, uri, true).getBody().toString();

        List<Long> eventViewsMap = new ArrayList<>(parseClientBackObjectToViews(body).values());
        Long eventViews = eventViewsMap.get(0);
        event.setViews(eventViews);

        return EventMapper.toFullEventDto(event);
    }

    @Transactional
    public List<FullEventDto> getUserEvents(Long userId, Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return eventRepository.findByInitiator(userId, page).stream().map(EventMapper::toFullEventDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FullEventDto> getEvents(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size, HttpServletRequest request) throws Exception {
        ResponseEntity<Object> hit = statsClient.saveHit(HitDto.builder().app("ewm-main-service").uri(request.getRequestURI())
                .ip(request.getRemoteAddr()).timestamp(LocalDateTime.now().format(formatter)).build());
        List<Event> events = new ArrayList<>(customEventRepository.findEvents(eventDynamicQueryDto, from, size));

        /*LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now();

        for (Event event : events) {
            if (event.getEventDate().isBefore(end)) {
                start = event.getEventDate();
            }
        }

        for (Event event : events) {
            if (event.getEventDate().isAfter(end)) {
                end = event.getEventDate();
            }
        }*/
        String start = LocalDateTime.now().minusYears(10L).format(formatter);
        String end = LocalDateTime.now().plusYears(10L).format(formatter);
        List<String> uri = new ArrayList<>();
        uri.add(request.getRequestURI());

        String body = statsClient.getStats(start, end, uri, true).getBody().toString();
        Map<Long, Long> countViews = parseClientBackObjectToViews(body);

        List<FullEventDto> fullEventDtos = events.stream().map(EventMapper::toFullEventDto).collect(Collectors.toList());

        for (FullEventDto event : fullEventDtos) {
            if (countViews.containsKey(event.getId())) {
                event.setViews(countViews.get(event.getId()));
            }
        }
        return fullEventDtos;

    }

    private Map<Long, Long> parseClientBackObjectToViews(String clientBackString) {
        String withoutBrackets = clientBackString.subSequence(1, clientBackString.length() - 1).toString();
        String[] splitRightBracket = withoutBrackets.split("}");
        Map<Long, Long> eventViews = new HashMap<>();
        if (splitRightBracket.length > 1) {
            for (String part : splitRightBracket) {
                eventViews.putAll(parseSubPartClientBackObjectToViews(part));
            }
        } else {
            eventViews.putAll(parseSubPartClientBackObjectToViews(splitRightBracket[0]));
        }

        return eventViews;
    }

    private Map<Long, Long> parseSubPartClientBackObjectToViews(String splitRightBracket) {
        Map<Long, Long> eventViews = new HashMap<>();
        String[] backHitDtoSplitString = splitRightBracket.split("\\,\\ hits\\=");
        Long countOfViews = Long.valueOf(backHitDtoSplitString[1]);
        String[] backHitDtoSplitStringLeftPart = backHitDtoSplitString[0].split("uri\\=");
        String[] backHitDtoSplitStringLeftPartRight = backHitDtoSplitStringLeftPart[1].split("/");
        try {
            Long event = Long.valueOf(backHitDtoSplitStringLeftPartRight[2]);
            eventViews.put(event, countOfViews);
            return eventViews;
        } catch (Exception e) {
            return eventViews;
        }

    }

    @Transactional
    public FullEventDto getUserEventById(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Event event = eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        return EventMapper.toFullEventDto(event);
    }
}
