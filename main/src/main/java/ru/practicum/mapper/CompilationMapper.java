package ru.practicum.mapper;

import ru.practicum.dto.CompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        Set<Event> events = compilation.getEvents();
        java.util.List<ru.practicum.dto.EventShortDto> eventShortDtos = null;

        if (events != null && !events.isEmpty()) {
            eventShortDtos = events.stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }

        return CompilationDto.builder()
                .id(compilation.getId())
                .events(eventShortDtos)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toCompilation(CompilationDto compilationDto) {
        if (compilationDto == null) {
            return null;
        }

        return Compilation.builder()
                .id(compilationDto.getId())
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build();
    }
}