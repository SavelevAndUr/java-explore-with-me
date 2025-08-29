package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting categories from {} size {}", from, size);
        return categoryService.getCategories(PageRequest.of(from, size));
    }

    @GetMapping(value = "/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("Getting category by id = {}", catId);
        return categoryService.getCategory(catId);
    }
}