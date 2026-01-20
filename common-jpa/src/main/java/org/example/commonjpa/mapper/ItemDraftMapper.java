package org.example.commonjpa.mapper;

import lombok.RequiredArgsConstructor;
import org.example.commonjpa.entity.ImageDraft;
import org.example.commonjpa.entity.ItemDraft;
import org.example.commonjpa.entity.dto.ItemDraftCreateDto;
import org.example.http.model.ItemModel;
import org.example.http.model.SizeModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ItemDraftMapper {

    private final ImageDraftMapper imageDraftMapper;

    public ItemDraft toEntity(ItemDraftCreateDto dto) {
        if (dto == null) return null;

        ItemDraft item = ItemDraft.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .weight(dto.getWeight())
                .itemId(dto.getItemId())
                .brand(dto.getBrand())
                .category(dto.getCategory())
                .quantity(dto.getQuantity())
                .sessionId(dto.getSessionId())
                .sizes(dto.getSizes())
                .images(new ArrayList<>())
                .build();

        if (dto.getImages() != null) {
            dto.getImages().forEach(imageDto -> {
                ImageDraft imageDraft = imageDraftMapper.toEntity(imageDto);
                item.addImage(imageDraft);
            });
        }

        return item;
    }

    public ItemDraftCreateDto toDto(ItemDraft entity) {
        if (entity == null) return null;

        return ItemDraftCreateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .weight(entity.getWeight())
                .itemId(entity.getItemId())
                .brand(entity.getBrand())
                .category(entity.getCategory())
                .quantity(entity.getQuantity())
                .sizes(entity.getSizes())
                .sessionId(entity.getSessionId())
                .images(imageDraftMapper.toDtoList(entity.getImages()))
                .build();
    }

    public ItemDraftCreateDto toDraft(ItemModel model) {
        if (model == null) {
            return null;
        }

        ItemDraftCreateDto.ItemDraftCreateDtoBuilder builder = ItemDraftCreateDto.builder();

        builder.name(model.getName());
        builder.description(model.getDescription());
        builder.price(model.getPrice());
        builder.weight(model.getWeight());
        builder.images(model.getImages().stream().map(imageDraftMapper::toDraft).toList());
        builder.quantity(model.getQuantity());

        builder.brand(model.getBrand() != null ? model.getBrand().getId() : null);
        builder.category(model.getCategory() != null ? model.getCategory().getId() : null);

        builder.sizes(model.getSizes().stream().map(SizeModel::getName).toList());

        return builder.build();
    }
}

