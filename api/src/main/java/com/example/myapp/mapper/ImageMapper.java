package com.example.myapp.mapper;

import com.example.myapp.model.Image;
import com.example.myapp.model.create.ImageCreateDto;
import com.example.myapp.model.dto.ImageDto;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper implements Mapper<Image, ImageDto, ImageCreateDto> {

    @Override
    public ImageDto toDto(Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .image(image.getImage())
                .isMain(image.isMain())
                .build();
    }

    @Override
    public Image toEntity(ImageCreateDto imageCreateDto) {
        return Image.builder()
                .image(imageCreateDto.getImage())
                .isMain(imageCreateDto.getIsMain())
                .build();
    }
}
