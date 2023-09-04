package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constant.CommonConstants;
import ru.practicum.enums.State;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RequestDto {
    @DateTimeFormat(pattern = CommonConstants.formatterToString)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.formatterToString)
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    State status;
}
