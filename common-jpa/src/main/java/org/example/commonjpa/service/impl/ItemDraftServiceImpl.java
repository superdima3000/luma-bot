package org.example.commonjpa.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.commonjpa.entity.ImageDraft;
import org.example.commonjpa.entity.ItemDraft;
import org.example.commonjpa.entity.dto.ImageDraftCreateDto;
import org.example.commonjpa.entity.dto.ItemDraftCreateDto;
import org.example.commonjpa.mapper.ImageDraftMapper;
import org.example.commonjpa.mapper.ItemDraftMapper;
import org.example.commonjpa.repository.ImageDraftRepository;
import org.example.commonjpa.repository.ItemDraftRepository;
import org.example.commonjpa.service.ItemDraftService;
import org.example.http.model.ItemModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemDraftServiceImpl implements ItemDraftService {

    private final ItemDraftRepository itemDraftRepository;
    private final ItemDraftMapper itemDraftMapper;
    private final ImageDraftMapper imageDraftMapper;
    private final ImageDraftRepository imageDraftRepository;

    @Override
    @Transactional
    public ItemDraft createItemDraft(ItemDraftCreateDto dto) {
        ItemDraft itemDraft = itemDraftMapper.toEntity(dto);
        return itemDraftRepository.save(itemDraft);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDraftCreateDto getItemDraft(Long sessionId) {
        ItemDraft itemDraft = itemDraftRepository.findBySessionId(sessionId);
        return itemDraftMapper.toDto(itemDraft);
    }

    @Override
    @Transactional
    public ItemDraft updateItemDraft(Long id, ItemDraftCreateDto dto) {
        ItemDraft existingDraft = itemDraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ItemDraft не найден с id: " + id));

        existingDraft.setName(dto.getName());
        existingDraft.setDescription(dto.getDescription());
        existingDraft.setPrice(dto.getPrice());
        existingDraft.setWeight(dto.getWeight());
        existingDraft.setBrand(dto.getBrand());
        existingDraft.setCategory(dto.getCategory());
        existingDraft.setSessionId(dto.getSessionId());
        existingDraft.setQuantity(dto.getQuantity());

        if (dto.getImages() != null) {
            existingDraft.getImages().clear();

            dto.getImages().forEach(imageDto -> {
                ImageDraft imageDraft;
                imageDraft = imageDraftRepository.findByImage(imageDto.getImage())
                            .orElseGet(() -> imageDraftMapper.toEntity(imageDto));

                existingDraft.addImage(imageDraft);
            });
        }

        return itemDraftRepository.save(existingDraft);
    }

    @Override
    @Transactional
    public void addSizeToItem(Long id, String size){
        var item = itemDraftRepository.findById(id).orElseThrow(() -> new RuntimeException("ItemDraft <UNK> <UNK> id: " + id));
        item.addSize(size);
        itemDraftRepository.save(item);
    }

    @Override
    @Transactional
    public void removeSizeFromItem(Long id, String size){
        var item = itemDraftRepository.findById(id).orElseThrow(() -> new RuntimeException("ItemDraft <UNK> <UNK> id: " + id));
        item.removeSize(size);
        itemDraftRepository.save(item);
    }

    @Override
    @Transactional
    public void removeImageFromItem(Long id, Long imageId){
        var imageDraft = imageDraftRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("ImageDraft not found."));
        var item = itemDraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ItemDraft <UNK> <UNK> id: " + id));
        item.removeImage(imageDraft);
        itemDraftRepository.save(item);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addImageToItem(Long id, String image, Boolean isMain) {
        ImageDraftCreateDto dto = ImageDraftCreateDto.builder()
                .image(image)
                .isMain(isMain)
                .build();

        var item = itemDraftRepository.findBySessionId(id);
        item.addImage(imageDraftMapper.toEntity(dto));
        itemDraftRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getItemSizes(Long draftId) {
        var item = itemDraftRepository.findById(draftId).orElseThrow(() -> new RuntimeException("ItemDraft <UNK> <UNK> <UNK> id: " + draftId));
        return new ArrayList<>(item.getSizes());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDraft> getItemImages(Long draftId) {
        var item = itemDraftRepository.findById(draftId)
                .orElseThrow(() -> new RuntimeException("ItemDraft <UNK> <UNK> <UNK> id: " + draftId));
        return new ArrayList<>(item.getImages());
    }

    @Override
    @Transactional
    public void deleteItemDraft(Long sessionId) {
        var item = itemDraftRepository.findBySessionId(sessionId);
        if (item != null) {
            itemDraftRepository.delete(item);
        }
    }

    @Override
    public ItemDraftCreateDto mapFromModel(ItemModel itemModel) {
        return itemDraftMapper.toDraft(itemModel);
    }
}
