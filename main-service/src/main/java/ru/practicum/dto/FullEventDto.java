package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constant.CommonConstants;
import ru.practicum.enums.State;
import ru.practicum.model.Category;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FullEventDto {
    Long id;//
    String annotation;//
    Category category;//
    Long confirmedRequests;//
    @DateTimeFormat(pattern = CommonConstants.formatterToString)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.formatterToString)
    LocalDateTime createdOn;//
    String description;//
    @DateTimeFormat(pattern = CommonConstants.formatterToString)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.formatterToString)
    LocalDateTime eventDate;//
    Long initiator;//
    Location location;//
    boolean paid;//
    Long participantLimit;//
    @DateTimeFormat(pattern = CommonConstants.formatterToString)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.formatterToString)
    LocalDateTime publishedOn;//
    boolean requestModeration;//
    State state;//
    String title;//
    Long views;//
}
