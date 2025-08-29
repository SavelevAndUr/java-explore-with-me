package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> createCompilation(@RequestParam(required = false, defaultValue = "false")
                                                  boolean pinned,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting compilations from {} size {} pinned={}", from, size, pinned);
        return compilationService.getCompilations(pinned, PageRequest.of(from, size));
    }

    @GetMapping(value = "{compId}")
    public CompilationDto createCompilation(@PathVariable Long compId) {
        log.info("Getting compilation with id={}", compId);
        return compilationService.getCompilation(compId);
    }
}