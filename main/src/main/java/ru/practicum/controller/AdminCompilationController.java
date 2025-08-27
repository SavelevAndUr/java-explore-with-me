package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;

import jakarta.validation.Valid;
import ru.practicum.validation.Marker;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Creating compilation {}", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping(value = "{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        log.info("Deleting compilation with Id={}", compId);
        compilationService.deleteById(compId);
    }

    @PatchMapping(value = "{compId}")
    public CompilationDto updateCompilation(@PathVariable Integer compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationDto) {
        log.info("Updating compilation {} with id={}", updateCompilationDto, compId);
        return compilationService.updateCompilation(updateCompilationDto, compId);
    }
}