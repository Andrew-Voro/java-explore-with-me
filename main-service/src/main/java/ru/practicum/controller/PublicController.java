package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.enums.Sort;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class PublicController {
    private final EventService eventService;
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategory(  ///
                                                              @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                              @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Категории запрошены.");

        return new ResponseEntity<>(categoryService.getAllCategory(from, size), HttpStatus.OK);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getAllCategory(@PathVariable("catId") Long catId) {     ///                                                    )
        {
            log.info("Категория с  catId: " + catId + " запрошена.");

            return new ResponseEntity<>(categoryService.getCategory(catId), HttpStatus.OK);
        }

    }

    ///events?text=0&categories=0&paid=true&rangeStart=2022-01-06%2013%3A30%3A38&rangeEnd=2097-09-06%2013%3A30%3A38
// &onlyAvailable=false&sort=EVENT_DATE&from=0&size=1000
    @GetMapping("/events")
    public ResponseEntity<List<FullEventDto>> getEventFiltered(@RequestParam(name = "text") Optional<String> text,  ///
                                                               @RequestParam(name = "categories") Optional<List<Long>> categories,
                                                               @RequestParam(name = "paid") Optional<Boolean> paid,
                                                               @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                               @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                               @RequestParam(name = "onlyAvailable") Optional<Boolean> onlyAvailable,
                                                               @RequestParam(name = "sort") Optional<Sort> sort,
                                                               @Min(0) @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                               @Min(0) @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime endTime = LocalDateTime.parse(rangeEnd, formatter);


        EventDynamicQueryDto eventDynamicQueryDto = EventDynamicQueryDto.builder().text(text).sort(sort)
                .onlyAvailable(onlyAvailable).categories(categories).rangeStart(startTime).rangeEnd(endTime)
                .paid(paid).build();

        return new ResponseEntity<>(eventService.getEvent(eventDynamicQueryDto, from, size), HttpStatus.OK);
    }
}
