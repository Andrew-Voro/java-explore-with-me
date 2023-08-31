package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constant.CommonConstants;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.enums.Sort;
import ru.practicum.handler.exception.ValidationException;
import ru.practicum.model.Compilation;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class PublicController {
    private final EventService eventService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategory(@Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                            @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Категории запрошены.");
        return new ResponseEntity<>(categoryService.getAllCategory(from, size), HttpStatus.OK);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getAllCategory(@PathVariable("catId") Long catId) {
        log.info("Категория с  id: " + catId + " запрошена.");
        return new ResponseEntity<>(categoryService.getCategory(catId), HttpStatus.OK);

    }


    @GetMapping("/events")
    public ResponseEntity<List<FullEventDto>> getEventFiltered(@RequestParam(name = "text") Optional<String> text,  ///
                                                               @RequestParam(name = "categories") Optional<List<Long>> categories,
                                                               @RequestParam(name = "paid") Optional<Boolean> paid,
                                                               @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                               @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                               @RequestParam(name = "onlyAvailable") Optional<Boolean> onlyAvailable,
                                                               @RequestParam(name = "sort") Optional<Sort> sort,
                                                               @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                               @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size,
                                                               HttpServletRequest request) throws Exception {

        EventDynamicQueryDto eventDynamicQueryDto;
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime startTime = LocalDateTime.parse(rangeStart, CommonConstants.formatter);
            LocalDateTime endTime = LocalDateTime.parse(rangeEnd, CommonConstants.formatter);
            if (endTime.isBefore(startTime)) {
                throw new ValidationException("rangeEnd не может быть раньше rangeStart");
            }
            eventDynamicQueryDto = EventDynamicQueryDto.builder().text(text).sort(sort)
                    .onlyAvailable(onlyAvailable).categories(categories).rangeStart(startTime).rangeEnd(endTime)
                    .paid(paid).build();
        } else {
            eventDynamicQueryDto = EventDynamicQueryDto.builder().text(text).sort(sort)
                    .onlyAvailable(onlyAvailable).categories(categories).paid(paid).build();
        }

        log.info("События запрошены.");
        return new ResponseEntity<>(eventService.getEvents(eventDynamicQueryDto, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<FullEventDto> getEventById(@PathVariable(name = "id") Long id, HttpServletRequest request) throws Exception {
        log.info("Событие с id: {} запрошено.", id);
        return new ResponseEntity<>(eventService.getEventById(id, request), HttpStatus.OK);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Compilation> getCompilationById(@PathVariable(name = "compId") Long compId) {
        log.info("Сборка с id: {} запрошена.", compId);
        return new ResponseEntity<>(compilationService.getCompilationById(compId), HttpStatus.OK);
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<Compilation>> getCompilations(@RequestParam(name = "pinned") Optional<Boolean> pinned,
                                                             @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                             @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) throws Exception {
        if (pinned.isEmpty()) {
            return new ResponseEntity<>(compilationService.getCompilations(from, size), HttpStatus.OK);
        }
        log.info("Сборки запрошены.");
        return new ResponseEntity<>(compilationService.getCompilations(pinned.get(), from, size), HttpStatus.OK);
    }
}
