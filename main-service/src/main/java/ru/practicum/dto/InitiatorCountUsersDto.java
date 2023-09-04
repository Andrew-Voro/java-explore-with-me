package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class InitiatorCountUsersDto {
    String Initiator;
    Long countOfSubscribers;
}
