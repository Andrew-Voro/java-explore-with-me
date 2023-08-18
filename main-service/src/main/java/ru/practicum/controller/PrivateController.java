package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.enums.State;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<FullEventDto> saveNewCategory(@Min(0) @PathVariable("userId") Long userId, @RequestBody @Valid EventDto eventDto) {
        if (eventDto.getStateAction() == null) {
            eventDto.setStateAction(State.PENDING);
        }
        return new ResponseEntity<>(eventService.saveEvent(userId, eventDto), HttpStatus.CREATED);

    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<FullEventDto>> getUserEvents(@PathVariable(name = "userId") Long userId,  ///
                                                            @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                            @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("События пользователя с  id: " + userId + " запрошены.");


        return new ResponseEntity<>(eventService.getUserEvents(userId, from, size), HttpStatus.OK);
    }

}
