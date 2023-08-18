package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.State;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EventDto {
    Long id;
    @NotNull
    @NotBlank
    String annotation;
    Long category;
    @NotNull
    @NotBlank
    String description;
    State stateAction;
    String eventDate;
    Location location;
    String title;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    }
