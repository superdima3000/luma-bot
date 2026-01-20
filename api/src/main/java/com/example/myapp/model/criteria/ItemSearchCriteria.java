package com.example.myapp.model.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemSearchCriteria {
    private String name;
    @Builder.Default
    private List<Long> brands = new ArrayList<>();
    @Builder.Default
    private List<Long> categories = new ArrayList<>();
    private Double minPrice;
    private Double maxPrice;
    private Integer minQuantity;
    @Builder.Default
    private List<String> sizes = new ArrayList<>();
}
