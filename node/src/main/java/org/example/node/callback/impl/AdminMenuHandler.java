package org.example.node.callback.impl;

import org.example.commonjpa.entity.enums.State;
import org.example.commonjpa.service.UserService;
import org.example.node.callback.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static org.example.commonjpa.entity.enums.State.*;

@Component
public class AdminMenuHandler extends AbstractCallbackHandler {

    private final UserService userService;

    public AdminMenuHandler(UserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    protected String getPrefix() {
        return "admin_menu:";
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long userId = callbackQuery.getFrom().getId();

        String callbackData = callbackQuery.getData();
        var parts = callbackData.split(":");
        String destination = parts[1];

        switch (destination) {
            case "admin_menu" -> {
                senderService.deleteMessage(chatId, messageId);
                menuService.showAdminMenu(chatId, messageId);
                answerCallbackQuery(callbackQuery.getId());
            }
            case "add" ->
            {
                adminService.addItem(chatId, messageId, userId);
                answerCallbackQuery(callbackQuery.getId());
            }
            case "edit" ->
            {
                adminService.editItem(chatId, messageId, userId);
                answerCallbackQuery(callbackQuery.getId());
            }

            case "edit_list" -> {
                senderService.deleteMessage(chatId, messageId);
                menuService.showAllItems(chatId, messageId);
            }

            case "add_category" -> {
                senderService.deleteMessage(chatId, messageId);
                adminService.addCategory(chatId, messageId, userId);
            }

            case "add_brand" -> {
                senderService.deleteMessage(chatId, messageId);
                adminService.addBrand(chatId, messageId, userId);
            }
            case "sizes_ready" -> {
                adminService.addItemBrand(chatId, messageId, userId);
                answerCallbackQuery(callbackQuery.getId());
            }
            case "brands" -> {
                var brandId = parts[2];
                adminService.addItemBrandProceed(chatId, messageId, userId, Long.parseLong(brandId));
                answerCallbackQuery(callbackQuery.getId());
            }

            case "edit_brands" -> {
                var brandId = parts[2];
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemBrandProceed(chatId, messageId, userId, Long.parseLong(brandId));
                answerCallbackQuery(callbackQuery.getId());
            }

            case "categories" -> {
                var categoryId = parts[2];
                adminService.addItemCategoryProceed(chatId, messageId, userId, Long.parseLong(categoryId));
                answerCallbackQuery(callbackQuery.getId());
            }

            case "edit_categories" -> {
                var categoryId = parts[2];
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemCategoryProceed(chatId, messageId, userId, Long.parseLong(categoryId));
                answerCallbackQuery(callbackQuery.getId());
            }

            case "item" -> {
                var itemId = parts[2];
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemMenu(chatId, messageId, userId, Long.parseLong(itemId));
            }

            case "edit_name" -> {
                var itemId = Long.parseLong(parts[2]);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                senderService.deleteMessage(chatId, messageId);
                senderService.sendAnswer(chatId,"Введите название: ");
                userService.changeState(userId, WAITING_FOR_ITEM_NAME_EDIT);
            }

            case "edit_description" -> {
                var itemId = Long.parseLong(parts[2]);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                senderService.deleteMessage(chatId, messageId);
                senderService.sendAnswer(chatId,"Введите описание вещи: ");
                userService.changeState(userId, WAITING_FOR_ITEM_DESCRIPTION_EDIT);
            }

            case "edit_price" -> {
                var itemId = Long.parseLong(parts[2]);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                senderService.deleteMessage(chatId, messageId);
                senderService.sendAnswer(chatId,"Введите цену вещи: ");
                userService.changeState(userId, WAITING_FOR_ITEM_PRICE_EDIT);
            }

            case "edit_quantity" -> {
                var itemId = Long.parseLong(parts[2]);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                senderService.deleteMessage(chatId, messageId);
                senderService.sendAnswer(chatId,"Введите наличие: ");
                userService.changeState(userId, WAITING_FOR_ITEM_QUANTITY_EDIT);
            }

            case "edit_category" -> {
                var itemId = Long.parseLong(parts[2]);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemCategory(chatId, messageId, userId);
            }

            case "edit_brand" -> {
                var itemId = Long.parseLong(parts[2]);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemBrand(chatId, messageId, userId);
            }

            case "edit_sizes" -> {
                var itemId = Long.parseLong(parts[2]);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemSizes(chatId, messageId, userId);
            }

            case "edit_sizes_add" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                userService.changeState(userId, WAITING_FOR_ITEM_SIZES_EDIT);
                senderService.sendAnswer(chatId,"Введите размер: ");
            }

            case "edit_sizes_delete" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemSizesRemoveMenu(chatId, messageId, userId);
            }

            case "delete_size" -> {
                var size = parts[2];
                senderService.deleteMessage(chatId, messageId);
                adminService.editItemSizesRemove(chatId, messageId, userId, size);
            }

            case "edit_sizes_ready" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                adminService.finalizeEditSizes(chatId, messageId, userId);
            }

            case "edit_images" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                adminService.editItemImages(chatId, messageId, userId);
            }

            case "edit_images_add" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                userService.changeState(userId, WAITING_FOR_ITEM_IMAGES_EDIT);
                senderService.sendAnswer(chatId,"Отправьте изображение: ");
            }

            case "edit_images_delete" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                adminService.editItemImagesRemoveMenu(chatId, messageId, userId);
            }

            case "delete_image" -> {
                var itemId = Long.parseLong(parts[2]);
                var imageId = Long.parseLong(parts[3]);
                senderService.deleteMessage(chatId, messageId);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                adminService.editItemImagesRemove(chatId, messageId, userId, imageId);
            }

            case "edit_images_ready" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                adminService.choseMainImage(chatId, messageId, userId);
            }

            case "main_image" -> {
                var itemId = Long.parseLong(parts[2]);
                var imageId = Long.parseLong(parts[3]);
                senderService.deleteMessage(chatId, messageId);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                adminService.finalizeEditImages(chatId, messageId, userId, imageId);

            }

            case "edit_save" -> {
                var itemId = Long.parseLong(parts[2]);
                senderService.deleteMessage(chatId, messageId);
                menuService.tryDeletingMediaGroup(chatId, itemId);
                adminService.finalizeEditItem(chatId, messageId, userId, itemId);
            }
        }
    }
}
