package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constant.CommonConstants;
import ru.practicum.dto.*;
import ru.practicum.dto.query.EventRequestConfirmQueryDto;
import ru.practicum.enums.State;
import ru.practicum.handler.exception.ConflictException;
import ru.practicum.handler.exception.ValidationException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;
import ru.practicum.service.SubscriptionService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;
    private final CategoryService categoryService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<FullEventDto> saveNewEvent(@Min(0) @PathVariable("userId") Long userId, @RequestBody @Valid EventDto eventDto) {
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0L);
        }
        if (eventDto.getRequestModeration() == null) {
            eventDto.setRequestModeration(true);
        }

        if (eventDto.getStateAction() == null) {
            eventDto.setStateAction(State.PENDING);
        }
        if (eventDto.getPaid() == null) {
            eventDto.setPaid(false);
        }

        if (eventDto.getDescription().length() < 20 || eventDto.getDescription().length() > 7000) {
            log.info("Description события  должно содержать больше или равно 20 символов или меньше либо равно 7000");
            EventMapper.toDtoEvent(userId, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, CategoryMapper
                    .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
        }
        if (eventDto.getAnnotation().length() < 20 || eventDto.getAnnotation().length() > 2000) {
            log.info("Annotation события  должно содержать больше или равно 20 символов или меньше либо равно 2000");
            EventMapper.toDtoEvent(userId, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, CategoryMapper
                    .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
        }

        if (eventDto.getTitle().length() < 3 || eventDto.getTitle().length() > 120) {
            log.info("Title события  должно содержать больше или равно 3 символов или меньше либо равно 120");
            EventMapper.toDtoEvent(userId, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, CategoryMapper
                    .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
        }


        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), CommonConstants.formatter);
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Нельзя менять дату события на уже наступившую");
        }
        return new ResponseEntity<>(eventService.saveEvent(userId, eventDto), HttpStatus.CREATED);

    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<FullEventDto>> getUserEvents(@PathVariable(name = "userId") Long userId,
                                                            @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                            @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("События пользователя с  id: " + userId + " запрошены.");


        return new ResponseEntity<>(eventService.getUserEvents(userId, from, size), HttpStatus.OK);
    }

    //users/:userId/events/:eventId
    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<FullEventDto> getUserEventById(@PathVariable(name = "userId") Long userId,  ///
                                                         @PathVariable(name = "eventId") Long eventId) {
        log.info("События пользователя с  id: " + userId + " и события с id: " + eventId + " запрошены.");


        return new ResponseEntity<>(eventService.getUserEventById(userId, eventId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<RequestDto> saveRequest(@Min(0) @PathVariable("userId") Long userId,
                                                  @Min(0) @RequestParam(name = "eventId") Long eventId) {

        RequestDto requestDto = RequestDto.builder().created(LocalDateTime.now()).event(eventId).requester(userId).status(State.PENDING).build();
        log.info("Пользователь с  id: " + userId + " сделал запрос.");
        return new ResponseEntity<>(requestService.saveRequest(userId, eventId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<RequestDto>> getUserRequest(@Min(0) @PathVariable("userId") Long userId) {
        log.info("Пользователь с  id: " + userId + " запрос сделанных им запросов.");
        return new ResponseEntity<>(requestService.getUserRequests(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> getUserEventRequest(@Min(0) @PathVariable("userId") Long userId,
                                                                @Min(0) @PathVariable("eventId") Long eventId) {
        log.info("Запрос пользователь с  id: " + userId + "событие с id: " + eventId);
        return new ResponseEntity<>(requestService.getUserEventRequest(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<FullEventDto> updateUserEvent(@Min(0) @PathVariable("userId") Long userId,
                                                        @Min(0) @PathVariable("eventId") Long eventId, @RequestBody EventDto eventDto) { //@Valid

        if (eventDto.getDescription() != null && (eventDto.getDescription().length() < 20 || eventDto.getDescription().length() > 7000)) {
            log.info("Description события id: {} должно содержать больше или равно 20 символов или меньше либо равно 7000", eventId);
            //EventMapper.toDtoEvent(userId, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            if (eventDto.getCategory() != null) {
                return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, CategoryMapper
                        .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
            } else
                return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, null)), HttpStatus.BAD_REQUEST);
        }
        if (eventDto.getAnnotation() != null && (eventDto.getAnnotation().length() < 20 || eventDto.getAnnotation().length() > 2000)) {
            log.info("Annotation события id: {} должно содержать больше или равно 20 символов или меньше либо равно 2000", eventId);
            //EventMapper.toDtoEvent(userId, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            if (eventDto.getCategory() != null) {
                return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, CategoryMapper
                        .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
            } else
                return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, null)), HttpStatus.BAD_REQUEST);
        }

        if (eventDto.getTitle() != null && (eventDto.getTitle().length() < 3 || eventDto.getTitle().length() > 120)) {
            log.info("Title события id: {} должно содержать больше или равно 3 символов или меньше либо равно 120", eventId);
            // EventMapper.toDtoEvent(userId, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            if (eventDto.getCategory() != null) {
                return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, CategoryMapper
                        .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
            } else
                return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(userId, eventDto, null)), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(eventService.updateUserEvent(eventDto, userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<RequestDto> updateRequestCancel(@Min(0) @PathVariable("userId") Long userId,
                                                          @Min(0) @PathVariable("requestId") Long requestId) {
        log.info("Запрос  id: {} отменен пользователем  id: {}", requestId, userId);
        return new ResponseEntity<>(requestService.updateRequestCancel(userId, requestId), HttpStatus.OK);
    }

    @PatchMapping("{userId}/events/{eventId}/requests")
    public ResponseEntity<RequestsDtoLists> updateRequestStatusByOwner(@Min(0) @PathVariable("userId") Long userId,
                                                                       @Min(0) @PathVariable("eventId") Long eventId,
                                                                       @RequestBody EventRequestConfirmQueryDto eventRequestConfirmQueryDto) {
        log.info("Запрос на событие id: {} обновлен пользователем  id: {}", eventId, userId);
        return new ResponseEntity<>(requestService.updateRequestStatusByOwner(userId, eventId, eventRequestConfirmQueryDto), HttpStatus.OK);
    }

    @PostMapping("/{userId}/subscription")
    public ResponseEntity<SubscriptionDto> saveSubscription(@Min(0) @PathVariable("userId") Long userId,
                                                            @Min(0) @RequestParam(name = "initiatorId") Long initiatorId) {
        if (userId.equals(initiatorId)) {
            throw new ConflictException("Пользоватеьль не может быть подписанным на себя.");
        }

        SubscriptionDto subscriptionDto = SubscriptionDto.builder().user(userId).initiator(initiatorId).build();
        log.info("Пользователь с  id: " + userId + " сделал запрос  на подписку на пользователя " + initiatorId);
        return new ResponseEntity<>(subscriptionService.saveSubscription(subscriptionDto), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/subscription")
    public ResponseEntity<SubscriptionDto> getSubscriptionByUserIdAndInitiatorId(@Min(1) @PathVariable("userId") Long userId,
                                                                                 @Min(1) @RequestParam(name = "initiatorId") Long initiatorId) {
        log.info("Запрос на поиск подписки пользователя с  id: " + userId + " пользователя с  id: " + initiatorId);
        return new ResponseEntity<>(subscriptionService.getSubscriptionByUserIdAndInitiatorId(userId, initiatorId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/subscription")
    public ResponseEntity<Void> deleteSubscriptionByUserIdAndInitiatorId(@Min(1) @PathVariable("userId") Long userId,
                                                                         @Min(1) @RequestParam(name = "initiatorId") Long initiatorId) {
        log.info("Запрос на удаление подписки пользователя с  id: " + userId + " пользователя с  id: " + initiatorId);
        subscriptionService.deleteSubscriptionByUserIdAndInitiatorId(userId, initiatorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/subscription/initiator/{initiatorId}")
    public ResponseEntity<List<User>> getSubscribedUsersByInitiatorId(@Min(1) @PathVariable("initiatorId") Long initiatorId,
                                                                      @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                                      @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Запрос на поиск подписчиков пользователя с  id: " + initiatorId);
        return new ResponseEntity<>(subscriptionService.getUsersOfInitiator(initiatorId, from, size), HttpStatus.OK);
    }

    @GetMapping("/subscription/{userId}")
    public ResponseEntity<List<User>> getInitiatorsByUserId(@Min(1) @PathVariable("userId") Long userId,
                                                            @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                            @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Запрос на поиск подписчиков пользователя с  id: " + userId);
        return new ResponseEntity<>(subscriptionService.getInitiatorsOfUser(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/subscription/events/{userId}")
    public ResponseEntity<List<Event>> getEventsOfSubscriptionsByUserId(@Min(1) @PathVariable("userId") Long userId,
                                                                        @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                                        @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Запрос на поиск подписчиков пользователя с  id: " + userId);
        return new ResponseEntity<>(subscriptionService.getEventsOfSubscriptionsByUserId(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/subscription/initiator/count")
    public ResponseEntity<List<InitiatorCountUsersDto>> getInitiatorOrderedByNumberOfUsers(@Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                                                           @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Запрос на поиск инициаторов упорядоченных по количеству пользователей");
        return new ResponseEntity<>(subscriptionService.getInitiatorOrderedByNumberOfUsers(from, size), HttpStatus.OK);
    }

}