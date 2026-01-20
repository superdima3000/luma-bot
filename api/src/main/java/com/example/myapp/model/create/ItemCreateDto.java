package com.example.myapp.model.create;

import com.example.myapp.model.dto.BrandDto;
import com.example.myapp.model.dto.CategoryDto;
import com.example.myapp.model.dto.ImageDto;
import com.example.myapp.model.dto.SizeDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCreateDto {
    @NotBlank(message = "Название не должно быть пустым.")
    @Size(max = 255, message = "Название не должно быть длинее 255 символов.")
    private String name;

    private String description;

    @PositiveOrZero(message = "Вводите только положительные числа.")
    @Digits(integer = 8, fraction = 2, message = "Неверный формат числа.")
    @NotNull
    private Double price;

    @PositiveOrZero(message = "Вводите только положительные числа.")
    @Digits(integer = 8, fraction = 2, message = "Неверный формат числа.")
    private Double weight;

    @NotEmpty(message = "Должно быть хотя бы одно изображение.")
    private List<@Valid ImageCreateDto> images;

    @NotNull(message = "Категория обязательна.")
    private Long brand;

    @NotNull(message = "Бренд обязателен.")
    private Long category;

    @NotNull(message = "Введите наличие товара.")
    @PositiveOrZero(message = "Вводите только положительные числа.")
    private Integer quantity;
}
