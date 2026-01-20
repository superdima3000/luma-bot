package org.example.commonjpa.entity.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDraftCreateDto {
    private String image;
    private Boolean isMain;
}
