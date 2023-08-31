package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.CompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation toDtoCompilation(CompilationDto compilationDto, Set<Event> events) {
        return new Compilation(
                compilationDto.getId(),
                compilationDto.getTitle(),
                events,
                compilationDto.getPinned()
        );
    }
}
