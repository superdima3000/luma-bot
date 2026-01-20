package org.example.http.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemModel {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("weight")
    private Double weight;
    @JsonProperty("images")
    private List<ImageModel> images;
    @JsonProperty("sizes")
    private List<SizeModel> sizes;
    @JsonProperty("brand")
    private BrandModel brand;
    @JsonProperty("category")
    private CategoryModel category;
    @JsonProperty("quantity")
    private Integer quantity;
}
