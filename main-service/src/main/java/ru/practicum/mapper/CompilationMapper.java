package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.CompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation toDtoCompilation(CompilationDto CompilationDto, List<Event> events) {
        return new Compilation(
                CompilationDto.getId(),
                CompilationDto.getTitle(),
                events,
                CompilationDto.getPinned()
        );
    }
}
