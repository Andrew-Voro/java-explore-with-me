package ru.practicum.service;

import ru.practicum.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(CategoryDto category);

    CategoryDto updateCategory(CategoryDto category, Long catId);

    void deleteCategory(Long id);

    List<CategoryDto> getAllCategory(Long from, Long size);

    CategoryDto getCategory(Long catId);

}
