package com.example.myapp.service.impl;

import com.example.myapp.exception.ConflictException;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.mapper.BrandMapper;
import com.example.myapp.model.Brand;
import com.example.myapp.model.create.BrandCreateDto;
import com.example.myapp.model.dto.BrandDto;
import com.example.myapp.repository.BrandRepository;
import com.example.myapp.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> getAllBrandsInStock(){
        return brandRepository.findBrandsInStock().stream()
                .map(brandMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto getBrandById(Long id) {
        Brand brand = findBrandByIdOrThrow(id);
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> getBrandsByName(String name) {
        var brands = brandRepository.findByNameContainingIgnoreCase(name);
        if (brands.isEmpty()) {
            throw new NotFoundException("Brand not found");
        }
        return brands.stream().map(brandMapper::toDto).toList();
    }

    @Override
    @Transactional
    public BrandDto createBrand(BrandCreateDto brandDto) {
        validateBrandNameNotExists(brandDto.getName());

        Brand brand = brandMapper.toEntity(brandDto);
        brand = brandRepository.save(brand);
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional
    public BrandDto updateBrand(Long id, BrandCreateDto brandCreateDto) {
        Brand existingBrand = findBrandByIdOrThrow(id);

        if (isNameChangedAndExists(existingBrand, brandCreateDto.getName())) {
            throw new ConflictException(
                    "Brand with name '" + brandCreateDto.getName() + "' already exists");
        }

        updateBrandFields(existingBrand, brandCreateDto);

        Brand updatedBrand = brandRepository.save(existingBrand);
        return brandMapper.toDto(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand existingBrand = findBrandByIdOrThrow(id);
        brandRepository.delete(existingBrand);
    }

    private Brand findBrandByIdOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand with id " + id + " not found"));
    }

    private void validateBrandNameNotExists(String name) {
        if (brandRepository.existsByName(name)) {
            throw new ConflictException("Name " + name + " already exists");
        }
    }

    private boolean isNameChangedAndExists(Brand existingBrand, String newName) {
        return !existingBrand.getName().equals(newName) &&
               brandRepository.existsByName(newName);
    }

    private void updateBrandFields(Brand brand, BrandCreateDto dto) {
        if (dto.getName() != null) {
            brand.setName(dto.getName());
        }
        if (dto.getImage() != null) {
            brand.setImage(dto.getImage());
        }
    }
}