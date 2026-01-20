package com.example.myapp.service;

import com.example.myapp.model.create.CategoryCreateDto;
import com.example.myapp.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    List<CategoryDto> getAllCategoriesInStock();
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getCategoriesByName(String name);
    CategoryDto createCategory(CategoryCreateDto CategoryDto);
    CategoryDto updateCategory(Long id, CategoryCreateDto CategoryDto);
    void deleteCategory(Long id);
    
}
