package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CompilationDto {
    Long id;
    @NotNull
    @NotBlank
    String title;
    List<Long> events;
    Boolean pinned;
}
