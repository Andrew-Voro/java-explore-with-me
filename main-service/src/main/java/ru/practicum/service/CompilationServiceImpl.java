package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.handler.exception.ObjectNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Transactional ///
    @Override
    public Compilation saveCompilation(CompilationDto compilationDto) {

        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }
        List<Event> events;
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findAllById(compilationDto.getEvents());
        } else {
            events = null;
        }

        return compilationRepository.save(CompilationMapper.toDtoCompilation(compilationDto, events));
    }

    @Transactional ///
    @Override
    public Compilation getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation not found"));
        return compilation;
    }

    @Transactional ///
    @Override
    public List<Compilation> getCompilations(Boolean pinned, Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return compilationRepository.findByPinned(pinned, page).stream().collect(Collectors.toList());
    }

    @Transactional ///
    @Override
    public void deleteCompilationById(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation not found"));
        compilationRepository.deleteById(compId);
    }

    @Transactional ///
    @Override
    public List<Compilation> getCompilations(Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return compilationRepository.findAll(page).stream().collect(Collectors.toList());
    }

    @Transactional ///
    @Override
    public Compilation patchCompilationById(Long compId, CompilationDto compilationDto) {
        List<Event> events;
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findAllById(compilationDto.getEvents());
        } else {
            events = null;
        }
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation not found"));
        compilation.setEvents(events);
        compilation.setPinned(compilationDto.getPinned());
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        return compilationRepository.save(compilation);
    }
}
