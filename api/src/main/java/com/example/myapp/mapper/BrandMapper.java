package com.example.myapp.mapper;

import com.example.myapp.model.Brand;
import com.example.myapp.model.create.BrandCreateDto;
import com.example.myapp.model.dto.BrandDto;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper implements Mapper<Brand, BrandDto, BrandCreateDto> {

    @Override
    public BrandDto toDto(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .image(brand.getImage())
                .build();
    }

    @Override
    public Brand toEntity(BrandCreateDto brandCreateDto) {
        return Brand.builder()
                .name(brandCreateDto.getName())
                .image(brandCreateDto.getImage())
                .build();
    }
}
