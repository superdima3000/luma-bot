package org.example.node.service;

import org.example.commonjpa.entity.dto.ItemDraftCreateDto;

public interface MenuService {
    void showBrandsList(Long chatId, Integer messageId);
    void showCategoriesList(Long chatId, Integer messageId);
    void showItemsList(Long chatId, Integer messageId);
    void showMainMenu(Long chatId, Integer messageId, boolean isAdmin);
    void tryDeletingMediaGroup(Long chatId, Long itemId);
    void showAdminMenu(Long chatId, Integer messageId);
    void showItemsForName(Long chatId, Integer messageId, String text);
    void showItemEditMenu(Long chatId, ItemDraftCreateDto draft);
    void showEditSizeMenu(Long chatId, ItemDraftCreateDto draft);
    void showEditSizeRemoveMenu(Long chatId, ItemDraftCreateDto draft);
    void showItemEditImagesMenu(Long chatId, ItemDraftCreateDto draft);
    void showEditImagesRemoveMenu(Long chatId, ItemDraftCreateDto draft);
    void showAllItems(Long chatId, Integer messageId);

    void chooseMainImageMenu(Long chatId, ItemDraftCreateDto draft);
}
