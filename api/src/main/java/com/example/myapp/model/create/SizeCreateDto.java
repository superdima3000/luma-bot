package com.example.myapp.model.create;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeCreateDto {
    @NotBlank(message = "Название не должно быть пустым.")
    @Size(max = 8, message = "Название не должно быть длинее 8 символов.")
    private String name;

    @PositiveOrZero(message = "Вводите только положительные числа.")
    @Digits(integer = 3, fraction = 2, message = "Неверный формат числа.")
    private Double chestCm;

    @PositiveOrZero(message = "Вводите только положительные числа.")
    @Digits(integer = 3, fraction = 2, message = "Неверный формат числа.")
    private Double waistCm;

    @PositiveOrZero(message = "Вводите только положительные числа.")
    @Digits(integer = 3, fraction = 2, message = "Неверный формат числа.")
    private Double lengthCm;

    @PositiveOrZero(message = "Вводите только положительные числа.")
    private Long itemId;
}
