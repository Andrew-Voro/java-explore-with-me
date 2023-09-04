package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class InitiatorCountUsersDto {
    String initiator;
    Long countOfSubscribers;
}
