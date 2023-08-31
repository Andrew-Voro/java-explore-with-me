package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.State;
import ru.practicum.model.Event;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FullRequestDto {
    Long id;
    LocalDateTime created;
    Event event;
    UserShortDto requester;
    State status;
}
