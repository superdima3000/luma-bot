package org.example.node.callback.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.http.model.ItemModel;
import org.example.node.callback.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CategoryCallbackHandler extends AbstractCallbackHandler {

    @Override
    protected String getPrefix() {
        return "category:";
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long categoryId = extractId(callbackQuery.getData());
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        showItemsForCategories(chatId, messageId, categoryId, callbackQuery);
    }

    public void showItemsForCategories(Long chatId, Integer messageId, Long categoryId, CallbackQuery callbackQuery){
        try {
            var items = clientService.getList("http://localhost:8082/api/items?quantity=1&category=" + categoryId, ItemModel.class);
            generateNewKeyboard(items,
                    callbackQuery.getId(),
                    chatId,
                    messageId,
                    "Товары категории:",
                    "category:" + categoryId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
