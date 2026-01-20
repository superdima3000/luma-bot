package com.example.myapp.service.impl;

import com.example.myapp.exception.ConflictException;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.mapper.CategoryMapper;
import com.example.myapp.model.Brand;
import com.example.myapp.model.Category;
import com.example.myapp.model.create.BrandCreateDto;
import com.example.myapp.model.create.CategoryCreateDto;
import com.example.myapp.model.dto.CategoryDto;
import com.example.myapp.repository.CategoryRepository;
import com.example.myapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoriesInStock() {
        return categoryRepository.findCategoriesInStock().stream()
                .map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        var category = findCategoryByIdOrThrow(id);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByName(String name) {
        var category = categoryRepository.findByNameContainingIgnoreCase(name);
        if (category.isEmpty()){
            throw new NotFoundException("Category not found");
        }

        return category.stream().map(categoryMapper::toDto).toList();
    }

    @Override
    public CategoryDto createCategory(CategoryCreateDto categoryDto) {
        validateCategoryNameNotExists(categoryDto.getName());
        var category = categoryMapper.toEntity(categoryDto);

        addSubCategory(category, categoryDto);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryCreateDto categoryCreateDto) {
        Category existingCategory = findCategoryByIdOrThrow(id);

        if (isNameChangedAndExists(existingCategory, categoryCreateDto.getName())) {
            throw new ConflictException(
                    "Category with name '" + categoryCreateDto.getName() + "' already exists");
        }

        updateCategoryFields(existingCategory, categoryCreateDto);
        addSubCategory(existingCategory, categoryCreateDto);

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category existingCategory = findCategoryByIdOrThrow(id);
        categoryRepository.delete(existingCategory);
    }

    private Category findCategoryByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }

    private void validateCategoryNameNotExists(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException("Name " + name + " already exists");
        }
    }

    private boolean isNameChangedAndExists(Category existingCategory, String newName) {
        return !existingCategory.getName().equals(newName) &&
               categoryRepository.existsByName(newName);
    }

    private void updateCategoryFields(Category category, CategoryCreateDto dto) {
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getParentId() != null) {
            category.setParent(categoryRepository.findById(dto.getParentId()).orElseThrow(
                    () -> new NotFoundException("Parent category with id " + dto.getParentId() + " not found")
            ));
        }
    }
    private void addSubCategory(Category category, CategoryCreateDto categoryCreateDto) {
        if (categoryCreateDto.getParentId() != null) {
            var parent = categoryRepository.findById(categoryCreateDto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));
            category.setParent(parent);
            parent.getSubCategories().add(category);
        }
    }
}
