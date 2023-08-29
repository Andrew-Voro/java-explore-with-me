package ru.practicum.dto.query;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.State;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EventRequestConfirmQueryDto {
    List<Long> requestIds;
    State status;
}
