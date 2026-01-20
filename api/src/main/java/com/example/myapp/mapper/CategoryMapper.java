package com.example.myapp.mapper;

import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.Category;
import com.example.myapp.model.create.CategoryCreateDto;
import com.example.myapp.model.dto.CategoryDto;
import com.example.myapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class CategoryMapper implements Mapper<Category, CategoryDto, CategoryCreateDto> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .subCategories(category.getSubCategories() != null
                        ? category.getSubCategories().stream().map(this::toDto).collect(toList())
                        : null)
                .build();
    }

    @Override
    public Category toEntity(CategoryCreateDto categoryCreateDto) {
        return Category.builder()
                .name(categoryCreateDto.getName())
                .build();
    }
}
