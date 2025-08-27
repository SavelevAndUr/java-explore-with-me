package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(Integer catId);

    CategoryDto updateCategory(Integer catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(PageRequest page);

    CategoryDto getCategory(Integer catId);

    Category getCategoryEntity(Integer catId);
}