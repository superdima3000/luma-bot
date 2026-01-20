package org.example.commonjpa.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commonjpa.entity.ImageDraft;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDraftCreateDto {
    @JsonIgnore
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double weight;
    private List<ImageDraftCreateDto> images;
    @Builder.Default
    @JsonIgnore
    private List<String> sizes = new ArrayList<>();
    private Long brand;
    private Long category;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonIgnore
    private Long itemId;
    @JsonIgnore
    private Long sessionId;
}
