package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation saveCompilation(CompilationDto compilation);

    Compilation getCompilationById(Long compId);

    List<Compilation> getCompilations(Boolean pinned, Long from, Long size);

    List<Compilation> getCompilations(Long from, Long size);

    void deleteCompilationById(Long compId);

    Compilation patchCompilationById(Long compId, CompilationDto compilation);
}
