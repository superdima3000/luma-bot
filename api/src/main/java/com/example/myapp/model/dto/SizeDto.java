package com.example.myapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeDto {
    private Long id;
    private String name;
    private Double chestCm;
    private Double waistCm;
    private Double lengthCm;
    private Long itemId;
}
