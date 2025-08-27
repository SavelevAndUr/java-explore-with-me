package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final String INCORRECT_DATA_INPUT_MSG = "Incorrect data input";
    private static final String INCORRECT_NAME_UNIQUE_REASON = "Field name must be unique";
    private static final String INCORRECT_CATEGORY_REL_REASON = "Category related to events";
    private static final String NOT_FOUND_CATEGORY_MSG = "Category not found";
    private static final String NOT_FOUND_ID_REASON = "Incorrect Id";

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (checkCategoryName(categoryDto, null)) {
            throw new ConflictException(INCORRECT_DATA_INPUT_MSG, INCORRECT_NAME_UNIQUE_REASON);
        }
        Category category = categoryRepository.save(CategoryMapper.toNewEntity(categoryDto));
        log.info("Created category {}", category.getId());
        return CategoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = getCategoryEntity(catId);

        if (!categoryRepository.findCategoryRelatedToEvents(category).isEmpty()) {
            throw new ConflictException(INCORRECT_DATA_INPUT_MSG, INCORRECT_CATEGORY_REL_REASON);
        }

        categoryRepository.deleteById(catId);
        log.info("Deleted category {} ", category);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        if (checkCategoryName(categoryDto, catId)) {
            throw new ConflictException(INCORRECT_DATA_INPUT_MSG, INCORRECT_NAME_UNIQUE_REASON);
        }
        Category category = categoryRepository.save(CategoryMapper.toEntity(catId, categoryDto));
        log.info("Updated category {}", category);
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(PageRequest page) {
        List<Category> categories = categoryRepository.findAllOrderById(page);
        log.info("Found categories {}", categories);
        return CategoryMapper.toDtos(categories);
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = getCategoryEntity(catId);
        return CategoryMapper.toDto(category);
    }

    @Override
    public Category getCategoryEntity(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CATEGORY_MSG, NOT_FOUND_ID_REASON));
        log.info("Found category {}", category);
        return category;
    }

    private boolean checkCategoryName(CategoryDto categoryDto, Long catId) {
        if (categoryDto.getName() == null) {
            return false;
        }
        return categoryRepository.findByName(categoryDto.getName())
                .map(Category::getId)
                .filter(id -> !Objects.equals(catId, id))
                .isPresent();
    }
}