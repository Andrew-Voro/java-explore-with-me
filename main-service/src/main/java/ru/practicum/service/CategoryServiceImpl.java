package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.handler.exception.ConflictException;
import ru.practicum.handler.exception.ObjectNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional ///
    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        String name = categoryDto.getName();
        if (categoryRepository.findByName(name).isEmpty()) {
            Category category = categoryRepository.save(CategoryMapper.toDtoCategory(categoryDto));
            return CategoryMapper.toCategoryDto(category);
        } else {
            throw new ConflictException("Категория с именем " + name + " уже существует.");
        }
    }

    @Transactional ///
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new ObjectNotFoundException("Category not found"));

        String name = categoryDto.getName();
        if (name != null) {
            Category categoryByName = categoryRepository.findByName(name).orElse(null);
            if (categoryByName == null || categoryByName.getId().equals(catId)) {
                category.setName(categoryDto.getName());
            } else {
                throw new ConflictException("Категория с именем " + name + " уже существует.");
            }
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public List<CategoryDto> getAllCategory(Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new ObjectNotFoundException("Category not found"));
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional ///
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Category not found"));
        List<Event> events = eventRepository.findBycategory(category);
        if (events.size() == 0) {
            categoryRepository.deleteById(id);
        } else {
            throw new ConflictException("Нельзя удалить не пустую категорию.");
        }
    }
}
