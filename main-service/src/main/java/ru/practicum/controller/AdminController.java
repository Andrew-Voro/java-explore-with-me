package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.enums.State;
import ru.practicum.handler.exception.ValidationException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;
import ru.practicum.service.UserService;

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

    private final UserService adminService;
    private final EventService eventService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventRepository eventRepository;

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

    @GetMapping("/events")
    public ResponseEntity<List<FullEventDto>> getEvent(@RequestParam(name = "users") Optional<List<Long>> users,  ///
                                                       @RequestParam(name = "states") Optional<List<State>> states,
                                                       @RequestParam(name = "categories") Optional<List<Long>> categories,
                                                       @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                       @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                       @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                       @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {

        if (rangeStart == null && rangeEnd == null) {

            EventDynamicQueryDto eventDynamicQueryDto = EventDynamicQueryDto.builder().users(users).states(states)
                    .categories(categories).build();
            return new ResponseEntity<>(eventService.getEventByAdmin(eventDynamicQueryDto, from, size), HttpStatus.OK);
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        LocalDateTime startTime = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime endTime = LocalDateTime.parse(rangeEnd, formatter);
        if (endTime.isBefore(startTime)) {
            throw new ValidationException("rangeEnd не может быть раньше rangeStart");
        }

        EventDynamicQueryDto eventDynamicQueryDto = EventDynamicQueryDto.builder().users(users).states(states)
                .categories(categories).rangeStart(startTime).rangeEnd(endTime).build();

        return new ResponseEntity<>(eventService.getEventByAdmin(eventDynamicQueryDto, from, size), HttpStatus.OK);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<FullEventDto> updateEvent(@Min(0) @PathVariable("eventId") Long eventId, @RequestBody EventDto eventDto) {///


        if (eventDto.getDescription() != null && (eventDto.getDescription().length() < 20 || eventDto.getDescription().length() > 7000)) {
            log.info("Description события  должно содержать больше или равно 20 символов или меньше либо равно 7000");
            EventMapper.toDtoEvent(0L, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(0L, eventDto, CategoryMapper
                    .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
        }
        if (eventDto.getAnnotation() != null && (eventDto.getAnnotation().length() < 20 || eventDto.getAnnotation().length() > 2000)) {
            log.info("Annotation события  должно содержать больше или равно 20 символов или меньше либо равно 2000");
            EventMapper.toDtoEvent(0L, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(0L, eventDto, CategoryMapper
                    .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
        }

        if (eventDto.getTitle() != null && (eventDto.getTitle().length() < 3 || eventDto.getTitle().length() > 120)) {
            log.info("Title события  должно содержать больше или равно 3 символов или меньше либо равно 120");
            EventMapper.toDtoEvent(0L, eventDto, CategoryMapper.toDtoCategory(categoryService.getCategory(eventDto.getCategory())));
            return new ResponseEntity<>(EventMapper.toFullEventDto(EventMapper.toDtoEvent(0L, eventDto, CategoryMapper
                    .toDtoCategory(categoryService.getCategory(eventDto.getCategory())))), HttpStatus.BAD_REQUEST);
        }
        log.info("Событие с  eventId: " + eventId + " обновлено.");
        return new ResponseEntity<>(eventService.updateEvent(eventDto, eventId), HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> saveNewCategory(@RequestBody @Valid CategoryDto category) {///
        if (category.getName().length() > 50) {
            log.info("Имя пользователя должно содержать больше или равно 2 символов или меньше либо равно 250");
            return new ResponseEntity<>(category, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.CREATED);

    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@Min(0) @PathVariable("catId") Long catId, @RequestBody @Valid CategoryDto category) {///
        if (category.getName().length() > 50) {
            log.info("Имя пользователя должно содержать больше или равно 2 символов или меньше либо равно 250");
            return new ResponseEntity<>(category, HttpStatus.BAD_REQUEST);
        }

        categoryService.getCategory(catId);
        log.info("Категория с  catId: " + catId + " обновлена.");
        return new ResponseEntity<>(categoryService.updateCategory(category, catId), HttpStatus.OK);
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
        //categoryService.getCategory(catId);
        log.info("Категория индексом catId: " + catId + " удалена.");
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/compilations")
    public ResponseEntity<Compilation> saveNewCompilation(@RequestBody @Valid CompilationDto compilation) {///
        if (compilation.getTitle().length() > 50) {
            log.info("Title сборки  должно содержать меньше или равно 50 символов ");

            return new ResponseEntity<>(CompilationMapper.toDtoCompilation(compilation, compilation.getEvents() != null ? eventRepository
                    .findAllById(compilation.getEvents()) : null), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(compilationService.saveCompilation(compilation), HttpStatus.CREATED);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Void> deleteCompilationById(@PathVariable(name = "compId") Long compId) {
        compilationService.deleteCompilationById(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/compilations/{compId}")
    public ResponseEntity<Compilation> patchCompilationById(@PathVariable(name = "compId") Long compId, @RequestBody CompilationDto compilation) {///
        if (compilation.getTitle() != null && compilation.getTitle().length() > 50) {
            log.info("Title сборки  должно содержать меньше или равно 50 символов ");
            return new ResponseEntity<>(CompilationMapper.toDtoCompilation(compilation, compilation.getEvents() != null ? eventRepository
                    .findAllById(compilation.getEvents()) : null), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(compilationService.patchCompilationById(compId, compilation), HttpStatus.OK);
    }
}
