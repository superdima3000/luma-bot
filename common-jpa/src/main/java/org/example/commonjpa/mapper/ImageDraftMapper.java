package org.example.commonjpa.mapper;

import org.example.commonjpa.entity.ImageDraft;
import org.example.commonjpa.entity.dto.ImageDraftCreateDto;
import org.example.http.model.ImageModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageDraftMapper {

    public ImageDraft toEntity(ImageDraftCreateDto dto) {
        if (dto == null) return null;

        return ImageDraft.builder()
                .image(dto.getImage())
                .isMain(dto.getIsMain())
                .build();
    }

    public ImageDraftCreateDto toDto(ImageDraft entity) {
        if (entity == null) return null;

        return ImageDraftCreateDto.builder()
                .image(entity.getImage())
                .isMain(entity.getIsMain())
                .build();
    }

    public List<ImageDraftCreateDto> toDtoList(List<ImageDraft> entities) {
        if (entities == null) return new ArrayList<>();

        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ImageDraftCreateDto toDraft(ImageModel model) {
        if (model == null) {
            return null;
        }

        // itemDraft здесь не заполняем, его обычно вешают снаружи
        return ImageDraftCreateDto.builder()
                .image(model.getImage())
                .isMain(model.getIsMain())
                .build();
    }
}
