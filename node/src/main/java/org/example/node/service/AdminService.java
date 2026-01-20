package org.example.node.service;

import lombok.NonNull;
import org.example.http.model.dto.ImageFileDto;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;

public interface AdminService {
    void addItem(Long chatId, Integer messageId, Long userId);
    void addItemName(Long chatId, Integer messageId, String name, Long userId);
    void addItemDescription(Long chatId, Integer messageId, String description, Long userId);
    void addItemPrice(Long chatId, Integer messageId, String price, Long userId);
    void addItemSizes(Long chatId, Integer messageId, String size, Long userId);
    void addItemBrand(Long chatId, Integer messageId, Long userId);
    void addItemBrandProceed(Long chatId, Integer messageId, Long userId, Long brandId);
    void clearDrafts(Long userId);
    void addItemCategoryProceed(Long chatId, Integer messageId, Long userId, Long categoryId);
    void addItemQuantity(Long chatId, Integer messageId, String quantity, Long userId);
    void addItemImages(Long chatId, Integer messageId, Long userId, List<PhotoSize> photo, String mediaGroupId);
    void addCategory(Long chatId, Integer messageId, Long userId);
    void addCategoryProceed(Long chatId, Integer messageId, String text, Long userId);
    void addBrand(Long chatId, Integer messageId, Long userId);
    void addBrandProceed(Long chatId, Integer messageId, String text, Long userId);
    void editItem(Long chatId, Integer messageId, Long userId);
    void showItemsForName(Long chatId, Integer messageId, String text, Long userId);
    void editItemMenu(Long chatId, Integer messageId, Long userId, Long itemId);
    void addItemNameEdit(Long chatId, Integer messageId, String text, Long userId);
    void addItemDescriptionEdit(Long chatId, Integer messageId, String text, Long userId);
    void addItemPriceEdit(Long chatId, Integer messageId, String text, Long userId);
    void addItemQuantityEdit(Long chatId, Integer messageId, String text, Long userId);
    void editItemCategory(Long chatId, Integer messageId, Long userId);
    void editItemCategoryProceed(Long chatId, Integer messageId, Long userId, Long categoryId);
    void editItemBrand(Long chatId, Integer messageId, Long userId);
    void editItemBrandProceed(Long chatId, Integer messageId, Long userId, Long brandId);
    void editItemSizes(Long chatId, Integer messageId, Long userId);
    void editItemSizesAdd(Long chatId, Integer messageId, Long userId, String size);
    void editItemSizesRemoveMenu(Long chatId, Integer messageId, Long userId);
    void editItemSizesRemove(Long chatId, Integer messageId, Long userId, String size);
    void finalizeEditSizes(Long chatId, Integer messageId, Long userId);
    void editItemImages(Long chatId, Integer messageId, Long userId);
    void editItemImagesAdd(Long chatId, Integer messageId, Long userId, List<PhotoSize> photo);
    void editItemImagesRemoveMenu(Long chatId, Integer messageId, Long userId);
    void editItemImagesRemove(Long chatId, Integer messageId, Long userId, Long imageId);
    void finalizeEditImages(Long chatId, Integer messageId, Long userId, Long imageId);
    void finalizeEditItem(Long chatId, Integer messageId, Long userId, Long itemId);
    void editItemImagesAddProceed(Long chatId, Long userId, ImageFileDto dto);

    void choseMainImage(Long chatId, Integer messageId, Long userId);
}
