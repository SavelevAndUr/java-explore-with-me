package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.RatingDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private static final String NOT_FOUND_COMPILATION_MSG = "Compilation not found";
    private static final String NOT_FOUND_ID_REASON = "Incorrect Id";
    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final RatingService ratingService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = getEvents(newCompilationDto);
        Compilation compilation = compilationRepository.save(CompilationMapper.toNewEntity(newCompilationDto, events));
        log.info("Created compilation {}", compilation);
        Map<Long, Integer> views = eventService.getStats(events);
        Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(events);
        return CompilationMapper.toDto(compilation, views, ratings);
    }

    @Override
    public void deleteById(Long compId) {
        if (compilationRepository.findById(compId).isPresent()) {
            compilationRepository.deleteById(compId);
        } else {
            throw new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON);
        }
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationDto, Long compId) {
        if (compilationRepository.findById(compId).isPresent()) {
            List<Event> events = new ArrayList<>();
            if (updateCompilationDto != null && updateCompilationDto.getEvents() != null) {
                List<Long> eventsId = new ArrayList<>(updateCompilationDto.getEvents());
                events = eventService.findByIds(eventsId);
            }
            Compilation compilation1 = compilationRepository.findById(compId)
                    .orElseThrow(() -> new NotFoundException("", ""));
            Compilation compilation = CompilationMapper.toEntity(updateCompilationDto, events, compilation1);
            compilation = compilationRepository.save(compilation);
            log.info("Updated compilation {}", compilation);
            Map<Long, Integer> views = eventService.getStats(events);
            Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(events);
            return CompilationMapper.toDto(compilation, views, ratings);
        } else {
            throw new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(boolean pinned, PageRequest page) {
        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, page);
        Set<Event> events = new HashSet<>();
        for (Compilation compilation : compilations) {
            events.addAll(compilation.getEvents());
        }
        Map<Long, Integer> views = new HashMap<>(eventService.getStats(new ArrayList<>(events)));
        Map<Long, RatingDto> ratings =  new HashMap<>(ratingService.getRatingsByEvents(new ArrayList<>(events)));
        return CompilationMapper.toDtos(compilations, views, ratings);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON));
        Map<Long, Integer> views = eventService.getStats(compilation.getEvents());
        return CompilationMapper.toDto(compilation, views);
    }

    private List<Event> getEvents(NewCompilationDto newCompilationDto) {
        if (newCompilationDto != null && newCompilationDto.getEvents() != null) {
            List<Long> eventsId = new ArrayList<>(newCompilationDto.getEvents());
            return eventService.findByIds(eventsId);
        } else {
            return Collections.emptyList();
        }
    }
}