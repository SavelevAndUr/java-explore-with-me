package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Creating category {}", categoryDto.toString());
        return categoryService.createCategory(categoryDto);

    }

    @DeleteMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer catId) {
        log.info("Deleting category with Id={}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping(value = "/{catId}")
    public CategoryDto updateCategory(@PathVariable Integer catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Updating category id={}", catId);
        return categoryService.updateCategory(catId, categoryDto);
    }
}