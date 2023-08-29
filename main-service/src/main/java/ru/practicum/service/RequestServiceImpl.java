package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestsDtoLists;
import ru.practicum.dto.query.EventRequestConfirmQueryDto;
import ru.practicum.enums.State;
import ru.practicum.handler.exception.ConflictException;
import ru.practicum.handler.exception.ObjectNotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public RequestDto saveRequest(Long userId, Long eventId, RequestDto requestDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("Запрос на участие в событии не требуется для его инициатора.");
        }
        if (!event.getStateAction().equals(State.PUBLISHED)) {
            throw new ConflictException("Запрос на участие в неопубликованном событии не возможен.");
        }
        /*if (requestRepository.findByEventAndStatus(event, State.CONFIRMED).size() >= event.getParticipantLimit()) {
            throw new ConflictException("Запрос на участие в  событии не возможен,превышен лимит участников.");
        }*/

        if (requestRepository.findByEventAndRequester(event, user).isEmpty()) {

            if (event.getParticipantLimit() <= requestRepository.findByEvent(event).stream().filter(x -> x.getStatus().equals(State.CONFIRMED)).collect(Collectors.toList()).size()
                    && event.getRequestModeration().equals(true) && event.getParticipantLimit() > 0) {
                requestRepository.save(RequestMapper.toDtoRequest(requestDto, user, event));///////посмотреть
                throw new ConflictException("Превышен лимит участников.");
            } else if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(false)) {
                requestDto.setStatus(State.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                eventRepository.save(event);
            } else if (event.getParticipantLimit() > event.getConfirmedRequests()) {

                eventRepository.save(event);
            } else {
                throw new ConflictException("Запрос на участие в  событии не возможен,превышен лимит участников.");
            }

            Request request = requestRepository.save(RequestMapper.toDtoRequest(requestDto, user, event));

            return RequestMapper.toRequestDto(request);
        } else {
            throw new ConflictException("Запрос на участие в этом событии уже был создан.");
        }
    }

    @Transactional
    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        //return requestRepository.findByRequester(user).stream().map(RequestMapper::toFullRequestDto).collect(Collectors.toList());
        return requestRepository.findByRequester(user).stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<RequestDto> getUserEventRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Event event = eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        return requestRepository.findByEvent(event).stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());

    }

    @Transactional
    @Override
    public RequestDto updateRequestCancel(Long userId, Long requestId) {
        /*Request request = requestRepository.findById(requestId).orElseThrow(() -> new ObjectNotFoundException("Request not found"));
        if(request.getEvent().getInitiator()==userId) {
            request.setStatus(State.CANCELED);
            return RequestMapper.toFullRequestDto(requestRepository.save(request));
        }

        return RequestMapper.toFullRequestDto(request);*/
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Request request = requestRepository.findByIdAndRequester(requestId, user);
        request.setStatus(State.CANCELED);
        Event event = eventRepository.findById(request.getEvent().getId()).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        eventRepository.save(event);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Transactional
    @Override
    public RequestsDtoLists updateRequestStatusByOwner(Long userId, Long eventId, EventRequestConfirmQueryDto eventRequestConfirmQueryDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Event event = eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("Лимит заявок на участие превышен");
        }
        List<Request> requests = requestRepository.findByEventAndIdIn(event, eventRequestConfirmQueryDto.getRequestIds());
       /* List<Request> requests = requestRepository.findByIdIn(eventRequestConfirmQueryDto.getRequestIds());
        List<RequestDto> requestsDtos =  requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("Лимит заявок на участие превышен");
        }
        if (!event.getConfirmedRequests().equals(event.getParticipantLimit())
                && (eventRequestConfirmQueryDto.getStatus().equals(State.CONFIRMED))) {
            confirmedRequests = requestsDtos
                    .stream()
                    .filter(o -> o.getStatus().equals(State.PENDING))
                    .collect(Collectors.toList());
            confirmedRequests.forEach(o -> o.setStatus(State.CONFIRMED));
            requestRepository.saveAll(confirmedRequests.stream().map(x->RequestMapper
                    .toDtoRequest(x,userRepository.findById(x.getRequester()).orElse(null),eventRepository
                            .findById(x.getEvent()).orElse(null))).collect(Collectors.toList()));
            event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
            eventRepository.save(event);
        }
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())
                || eventRequestConfirmQueryDto.getStatus().equals(State.REJECTED)) {
            rejectedRequests = requestsDtos
                    .stream()
                    .filter(o -> o.getStatus().equals(State.PENDING))
                    .collect(Collectors.toList());
            rejectedRequests.forEach(o -> o.setStatus(State.REJECTED));
            requestRepository.saveAll(rejectedRequests.stream().map(x->RequestMapper.toDtoRequest(x,userRepository
                    .findById(x.getRequester()).orElse(null),eventRepository
                    .findById(x.getEvent()).orElse(null))).collect(Collectors.toList()));
        }*/
        Long participantLimit = event.getParticipantLimit();
        Long confirmedRequests = event.getConfirmedRequests();
        Boolean unConfirmed = false;
        for (Request request : requests) {
            State status = eventRequestConfirmQueryDto.getStatus();
            if (status.equals(State.REJECTED) && request.getStatus().equals(State.CONFIRMED)) {
                throw new ConflictException("Нельзя отменить подтвержденный запрос на участие");
            }
            request.setStatus(status);
            if (status.equals(State.CONFIRMED)) {
                if (participantLimit > confirmedRequests) {
                    event.setConfirmedRequests(confirmedRequests + 1);
                    eventRepository.save(event);
                } else {
                    unConfirmed = true;
                    break;
                }
            }
        }
        if (unConfirmed) {
            requestRepository.saveAll(requests);
            throw new ConflictException("Лимит заявок на участие превышен");
        }

        List<Request> list = requestRepository.saveAll(requests);
        List<RequestDto> confirmed = list.stream().filter(x -> x.getStatus().equals(State.CONFIRMED)).map(RequestMapper::toRequestDto).collect(Collectors.toList());
        List<RequestDto> rejected = list.stream().filter(x -> x.getStatus().equals(State.REJECTED)).map(RequestMapper::toRequestDto).collect(Collectors.toList());
        return RequestsDtoLists.builder().confirmedRequests(confirmed).rejectedRequests(rejected).build();

    }
}
