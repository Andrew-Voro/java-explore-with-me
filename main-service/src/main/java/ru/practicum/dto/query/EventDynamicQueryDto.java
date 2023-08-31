package ru.practicum.dto.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constant.CommonConstants;
import ru.practicum.enums.Sort;
import ru.practicum.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EventDynamicQueryDto {

    Optional<List<Long>> users;
    Optional<List<State>> states;
    Optional<List<Long>> categories;
    @DateTimeFormat(pattern = CommonConstants.formatterToString)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.formatterToString)
    LocalDateTime rangeStart;
    @DateTimeFormat(pattern = CommonConstants.formatterToString)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.formatterToString)
    LocalDateTime rangeEnd;
    Optional<String> text;
    Optional<Boolean> paid;
    Optional<Boolean> onlyAvailable;
    Optional<Sort> sort;
}
