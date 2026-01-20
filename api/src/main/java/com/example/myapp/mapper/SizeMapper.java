package com.example.myapp.mapper;

import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.Size;
import com.example.myapp.model.create.SizeCreateDto;
import com.example.myapp.model.dto.SizeDto;
import com.example.myapp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SizeMapper implements Mapper<Size, SizeDto, SizeCreateDto> {

    @Override
    public SizeDto toDto(Size size) {
        return SizeDto.builder()
                .id(size.getId())
                .name(size.getName())
                .chestCm(size.getChestCm())
                .lengthCm(size.getLengthCm())
                .waistCm(size.getWaistCm())
                .itemId(size.getItem().getId())
                .build();
    }

    @Override
    public Size toEntity(SizeCreateDto sizeCreateDto) {
        return Size.builder()
                .name(sizeCreateDto.getName())
                .chestCm(sizeCreateDto.getChestCm())
                .lengthCm(sizeCreateDto.getLengthCm())
                .waistCm(sizeCreateDto.getWaistCm())
                .build();
    }
}
