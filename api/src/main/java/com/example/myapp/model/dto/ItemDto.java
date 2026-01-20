package com.example.myapp.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double weight;
    private List<ImageDto> images;
    private List<SizeDto> sizes;
    private BrandDto brand;
    private CategoryDto category;
    private Integer quantity;
}
