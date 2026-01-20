package org.example.http.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SizeModel {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("chestCm")
    private Double chestCm;
    @JsonProperty("waistCm")
    private Double waistCm;
    @JsonProperty("lengthCm")
    private Double lengthCm;
    @JsonProperty("itemId")
    private Long itemId;
}