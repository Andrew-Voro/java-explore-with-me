package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RequestsDtoLists {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
