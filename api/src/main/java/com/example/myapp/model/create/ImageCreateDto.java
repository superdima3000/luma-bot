package com.example.myapp.model.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageCreateDto {
    @NotBlank(message = "Название не должно быть пустым.")
    @Size(max = 255, message = "Название не должно быть длинее 255 символов.")
    private String image;

    private Boolean isMain;
}
