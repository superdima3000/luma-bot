package com.example.myapp.IT;

import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.create.CategoryCreateDto;
import com.example.myapp.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CategoryServiceIT {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void findAll() {
        var result = categoryService.getAllCategories();
        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    public void findById() {
        var result = categoryService.getCategoryById(2L);
        System.out.println(result);
        assertNotNull(result);

        assertThrows(NotFoundException.class,
                () -> categoryService.getCategoryById(1488L));
    }

    @Test
    public void save() {
        var category = CategoryCreateDto.builder()
                .name("Дилдо")
                .parentId(1L)
                .build();

        var result = categoryService.createCategory(category);
        System.out.println(result);
        assertNotNull(result);

    }

    @Test
    public void delete() {
        categoryService.deleteCategory(1L);
        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    public void update() {
        var category = CategoryCreateDto.builder()
                .name("Дилдо")
                .parentId(2L)
                .build();
        var result = categoryService.updateCategory(10L, category);
        System.out.println(result);
        assertNotNull(result);

    }
}
