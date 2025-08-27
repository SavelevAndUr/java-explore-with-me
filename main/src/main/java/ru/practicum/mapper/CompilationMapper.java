package ru.practicum.mapper;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.*;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toNewEntity(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toDto(Compilation compilation, Map<Integer, Integer> views) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.toShortDtos(compilation.getEvents(), views))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toEntity(UpdateCompilationRequest newCompilationDto, List<Event> events,
                                       Compilation compilation) {
        return Compilation.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(Objects.isNull(newCompilationDto.getTitle())
                        ? compilation.getTitle() : newCompilationDto.getTitle())
                .build();
    }

    public static Compilation toEntity(NewCompilationDto newCompilationDto, List<Event> events,
                                       Compilation compilation) {
        return Compilation.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(Objects.isNull(newCompilationDto.getTitle())
                        ? compilation.getTitle() : newCompilationDto.getTitle())
                .build();
    }

    public static List<CompilationDto> toDtos(List<Compilation> compilations, Map<Integer, Integer> views) {
        List<CompilationDto> dtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            dtos.add(toDto(compilation, views));
        }
        return dtos;
    }
}