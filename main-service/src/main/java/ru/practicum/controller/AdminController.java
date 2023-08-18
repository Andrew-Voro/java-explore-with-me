package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.enums.State;
import ru.practicum.handler.exception.ValidationException;
import ru.practicum.service.AdminService;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final EventService eventService;
    private final CategoryService categoryService;

    @PostMapping("/users")
    public ResponseEntity<UserDto> saveNewUser(@RequestBody @Valid UserDto user) { ///

        if (user.getEmail() == null) {
            throw new ValidationException("Not found email in body of request ");
        }

        if (user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            log.info("Некорректный почтовый адрес");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (user.getName() == null) {
            throw new ValidationException("Not found property name in body of request ");
        }

        if (user.getName().isBlank()) {
            log.info("Пустое имя пользователя");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (user.getName().length() < 2 || user.getName().length() > 250) {
            log.info("Имя пользователя должно содержать больше или равно 2 символов или меньше либо равно 250");
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }

        if (user.getEmail().length() < 6 || user.getEmail().length() > 254) {
            log.info("Email пользователя должен содержать больше или равно 6 символов или меньше либо равно 254");
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }

        log.info("Пользователь с почтой: " + user.getEmail() + " создан.");

        return new ResponseEntity<>(adminService.saveUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUser(@RequestParam(name = "ids") Optional<List<Long>> ids,  ///
                                                 @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                 @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Пользователь с  id: " + ids + " запрошен.");
        if (ids.isEmpty()) {
            return new ResponseEntity<>(adminService.getUser(from, size), HttpStatus.OK);
        }

        return new ResponseEntity<>(adminService.getUser(ids.get()), HttpStatus.OK);
    }


    //    /events?users=0&states=PUBLISHED&categories=0&rangeStart=2022-01-06%2013%3A30%3A38&rangeEnd=2097-09-06%2013%3A30%3A38&from=0&size=1000
    @GetMapping("/events")
    public ResponseEntity<List<FullEventDto>> getEvent(@RequestParam(name = "users") Optional<List<Long>> users,  ///
                                                       @RequestParam(name = "states") Optional<List<State>> states,
                                                       @RequestParam(name = "categories") Optional<List<Long>> categories,
                                                       @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                       @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                       @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                       @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime endTime = LocalDateTime.parse(rangeEnd, formatter);


        EventDynamicQueryDto eventDynamicQueryDto = EventDynamicQueryDto.builder().users(users).states(states)
                .categories(categories).rangeStart(startTime).rangeEnd(endTime).build();

        return new ResponseEntity<>(eventService.getEvent(eventDynamicQueryDto, from, size), HttpStatus.OK);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<FullEventDto> updateEvent(@Min(0) @PathVariable("eventId") Long eventId, @RequestBody EventDto eventDto) {///



       /* if (eventDto.getStateAction() == null) {
            eventDto.setStateAction(State.PENDING);
        }*/
        log.info("Событие с  eventId: " + eventId + " обновлено.");
        return new ResponseEntity<>(eventService.updateEvent(eventDto, eventId), HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> saveNewCategory(@RequestBody @Valid CategoryDto category) {///
        if (category.getName().length() > 50) {
            log.info("Имя пользователя должно содержать больше или равно 2 символов или меньше либо равно 250");
            return new ResponseEntity<>(category, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(adminService.saveCategory(category), HttpStatus.CREATED);

    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@Min(0) @PathVariable("catId") Long catId, @RequestBody @Valid CategoryDto category) {///
        if (category.getName().length() > 50) {
            log.info("Имя пользователя должно содержать больше или равно 2 символов или меньше либо равно 250");
            return new ResponseEntity<>(category, HttpStatus.BAD_REQUEST);
        }

        categoryService.getCategory(catId);
        log.info("Категория с  catId: " + catId + " обновлена.");
        return new ResponseEntity<>(adminService.updateCategory(category, catId), HttpStatus.OK);
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        if (id < 0) {
            log.info("Введите положительный id.");
            throw new ValidationException("delete: Введите положительный id.");
        }
        adminService.getUser(id);
        log.info("Пользователь с  id: " + id + " удален.");
        adminService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("catId") Long catId) {
        if (catId < 0) {
            log.info("Введите положительный catId.");
            throw new ValidationException("delete: Введите положительный catId.");
        }
        categoryService.getCategory(catId);
        log.info("Категория индексом catId: " + catId + " удалена.");
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
