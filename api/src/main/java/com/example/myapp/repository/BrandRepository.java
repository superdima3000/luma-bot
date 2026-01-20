package com.example.myapp.repository;

import com.example.myapp.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);
    List<Brand> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT b FROM Brand b JOIN Item i ON i.brand.id = b.id WHERE i.quantity > 0")
    List<Brand> findBrandsInStock();

}
