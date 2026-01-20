package com.example.myapp.controller;

import com.example.myapp.model.Brand;
import com.example.myapp.model.create.BrandCreateDto;
import com.example.myapp.model.dto.BrandDto;
import com.example.myapp.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandDto>> getBrands(@RequestParam(required = false) String brandName,
                                                    @RequestParam(required = false, defaultValue = "false") Boolean quantity) {
        if (brandName == null) {
            if (quantity) {
                return ResponseEntity.ok().body(brandService.getAllBrandsInStock());
            }
            return ResponseEntity.ok().body(brandService.getAllBrands());
        }

        return ResponseEntity.ok().body(brandService.getBrandsByName(brandName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrand(@PathVariable Long id) {
        return ResponseEntity.ok().body(brandService.getBrandById(id));
    }

    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@RequestBody @Valid BrandCreateDto brand) {
        var created = brandService.createBrand(brand);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable Long id, @RequestBody @Valid BrandCreateDto brand) {
        return ResponseEntity.ok().body(brandService.updateBrand(id, brand));
    }
}
