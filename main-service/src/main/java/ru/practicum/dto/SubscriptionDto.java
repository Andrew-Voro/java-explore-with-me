package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class SubscriptionDto {
    Long id;
    Long user;
    Long initiator;
}
