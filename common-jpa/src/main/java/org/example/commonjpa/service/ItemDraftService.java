package org.example.commonjpa.service;

import org.example.commonjpa.entity.ImageDraft;
import org.example.commonjpa.entity.ItemDraft;
import org.example.commonjpa.entity.dto.ItemDraftCreateDto;
import org.example.http.model.ItemModel;

import java.util.List;

public interface ItemDraftService {
    ItemDraft createItemDraft(ItemDraftCreateDto dto);
    ItemDraft updateItemDraft(Long id, ItemDraftCreateDto dto);
    ItemDraftCreateDto getItemDraft(Long sessionId);
    void addSizeToItem(Long id, String size);
    void addImageToItem(Long id, String image, Boolean isMain);
    List<String> getItemSizes(Long draftId);
    void deleteItemDraft(Long sessionId);
    ItemDraftCreateDto mapFromModel(ItemModel itemModel);
    void removeSizeFromItem(Long id, String size);
    void removeImageFromItem(Long id, Long imageId);
    List<ImageDraft> getItemImages(Long draftId);
}
