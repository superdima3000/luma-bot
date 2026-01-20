package org.example.node.callback.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.example.commonjpa.entity.MediaGroupModel;
import org.example.http.model.BrandModel;
import org.example.http.model.CategoryModel;
import org.example.node.callback.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class BackCallbackHandler extends AbstractCallbackHandler {

    @Autowired
    private BrandCallbackHandler brandCallback;
    @Autowired
    private CategoryCallbackHandler categoryCallbackHandler;

    @Override
    protected String getPrefix(){
        return "back:";
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        log.debug("Received data: " + data);
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        String[] parts = data.split(":");
        String destination = parts[1];
        if ("brand".equals(destination)) {
            showBrandsList(chatId, messageId);
        } else if ("category".equals(destination)) {
            showCategoriesList(chatId, messageId);
        } else if ("items".equals(destination)) {
            Long itemId = Long.parseLong(parts[2]);
            String sourceType = parts[3];
            Long sourceId = Long.parseLong(parts[4]);

            tryDeletingMediaGroup(chatId, itemId);
            if ("brand".equals(sourceType)) {
                showItemsForBrands(chatId, messageId, sourceId, callbackQuery);
            } else if ("category".equals(sourceType)) {
                showItemsForCategories(chatId, messageId, sourceId, callbackQuery);
            } else if ("stock".equals(sourceType)) {
                showStockList(chatId, messageId);
            }

        }
    }

    private void tryDeletingMediaGroup(Long chatId, Long itemId) {
        menuService.tryDeletingMediaGroup(chatId, itemId);
    }

    private void showBrandsList(Long chatId, Integer messageId) {
        log.debug("Showing brands list");
        menuService.showBrandsList(chatId, messageId);
        senderService.deleteMessage(chatId, messageId);
    }

    private void showStockList(Long chatId, Integer messageId) {
        menuService.showItemsList(chatId, messageId);
        senderService.deleteMessage(chatId, messageId);
    }

    private void showCategoriesList(Long chatId, Integer messageId) {
        menuService.showCategoriesList(chatId, messageId);
        senderService.deleteMessage(chatId, messageId);
    }

    private void showItemsForBrands(Long chatId, Integer messageId, Long sourceId, CallbackQuery callbackQuery) {
        brandCallback.showItemsForBrand(chatId, messageId, sourceId, callbackQuery);
    }

    private void showItemsForCategories(Long chatId, Integer messageId, Long sourceId, CallbackQuery callbackQuery) {
        categoryCallbackHandler.showItemsForCategories(chatId, messageId, sourceId, callbackQuery);
    }
}
