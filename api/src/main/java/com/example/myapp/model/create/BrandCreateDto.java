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
public class BrandCreateDto {

    @NotBlank(message = "Название не должно быть пустым.")
    @Size(max = 32, message = "Название не должно быть длинее 32 символов.")
    private String name;

    @Size(max = 255, message = "Путь до изображения слишком длинный.")
    private String image;
}
