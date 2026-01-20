package com.example.myapp.service;

import com.example.myapp.model.Brand;
import com.example.myapp.model.create.BrandCreateDto;
import com.example.myapp.model.dto.BrandDto;

import java.util.List;

public interface BrandService {
    List<BrandDto> getAllBrands();
    List<BrandDto> getAllBrandsInStock();
    BrandDto getBrandById(Long id);
    List<BrandDto> getBrandsByName(String name);
    BrandDto createBrand(BrandCreateDto brandDto);
    BrandDto updateBrand(Long id, BrandCreateDto brandDto);
    void deleteBrand(Long id);
}
