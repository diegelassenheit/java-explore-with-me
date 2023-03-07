package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoNew;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.util.Page;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto createCategory(CategoryDtoNew dto) {
        Category entity = categoryMapper.toModel(dto);
        return categoryMapper.toCategoryDto(categoryRepository.save(entity));
    }

    public void deleteCategory(Long catId) {
        checkCategory(catId);
        categoryRepository.deleteById(catId);
    }

    public CategoryDto updateCategory(Long catId, CategoryDtoNew dto) {
        checkCategory(catId);
        Category entity = categoryRepository.getReferenceById(catId);

        if (!entity.getName().equals(dto.getName())) {
            entity.setName(dto.getName());
            return categoryMapper.toCategoryDto(categoryRepository.save(entity));
        }
        return categoryMapper.toCategoryDto(entity);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(new Page(from, size))
                .toList().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId) {
        checkCategory(catId);
        return categoryMapper.toCategoryDto(categoryRepository.getReferenceById(catId));
    }

    private void checkCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category by id=" + catId + " was not found.");
        }
    }
}