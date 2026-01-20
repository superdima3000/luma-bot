package com.example.myapp.repository;

import com.example.myapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
    @Query("SELECT DISTINCT c FROM Category c JOIN Item i ON i.category.id = c.id WHERE i.quantity > 0")
    List<Category> findCategoriesInStock();
}
