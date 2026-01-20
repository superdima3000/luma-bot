package com.example.myapp.controller;

import com.example.myapp.model.create.CategoryCreateDto;
import com.example.myapp.model.dto.CategoryDto;
import com.example.myapp.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(required = false) String name,
                                                           @RequestParam(required = false, defaultValue = "false") Boolean quantity) {
        if (name == null) {
            if (quantity) {
                return ResponseEntity.ok().body(categoryService.getAllCategoriesInStock());
            }
            return ResponseEntity.ok().body(categoryService.getAllCategories());
        }

        return ResponseEntity.ok().body(categoryService.getCategoriesByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryCreateDto category) {
        var created = categoryService.createCategory(category);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,
                                                   @RequestBody @Valid CategoryCreateDto category) {
        return ResponseEntity.ok().body(categoryService.updateCategory(id, category));
    }
}
