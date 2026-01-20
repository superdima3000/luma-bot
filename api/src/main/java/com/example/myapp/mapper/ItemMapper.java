package com.example.myapp.mapper;

import com.example.myapp.model.Item;
import com.example.myapp.model.create.ItemCreateDto;
import com.example.myapp.model.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemMapper implements Mapper<Item, ItemDto, ItemCreateDto> {

    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    private final ImageMapper imageMapper;
    private final SizeMapper sizeMapper;

    @Override
    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .description(item.getDescription())
                .weight(item.getWeight())
                .brand(brandMapper.toDto(item.getBrand()))
                .category(categoryMapper.toDto(item.getCategory()))
                .images(item.getImages() != null
                        ? item.getImages().stream().map(imageMapper::toDto).toList()
                        : null)
                .sizes(item.getSizes() != null
                        ? item.getSizes().stream().map(sizeMapper::toDto).toList()
                        : null)
                .build();
    }

    public Item toEntity(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .price(itemCreateDto.getPrice())
                .description(itemCreateDto.getDescription())
                .weight(itemCreateDto.getWeight())
                .quantity(itemCreateDto.getQuantity())
                .build();
    }

}
