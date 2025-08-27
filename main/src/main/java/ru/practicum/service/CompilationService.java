package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteById(Integer compId);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationDto, Integer compId);

    List<CompilationDto> getCompilations(boolean pinned, PageRequest page);

    CompilationDto getCompilation(Integer compId);
}