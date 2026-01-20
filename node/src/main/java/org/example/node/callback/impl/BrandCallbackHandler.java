package org.example.node.callback.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.example.http.model.ItemModel;
import org.example.http.service.ClientService;
import org.example.node.callback.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class BrandCallbackHandler extends AbstractCallbackHandler {

    @Override
    protected String getPrefix(){
        return "brands:";
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long brandId = extractId(callbackQuery.getData());
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        showItemsForBrand(chatId, messageId, brandId, callbackQuery);
    }

    public void showItemsForBrand(Long chatId, Integer messageId, Long brandId, CallbackQuery callbackQuery) {
        try {
            var items = clientService.getList("http://localhost:8082/api/items?quantity=1&brand=" + brandId, ItemModel.class);
            generateNewKeyboard(items,
                    callbackQuery.getId(),
                    chatId,
                    messageId,
                    "Товары бренда:",
                    "brand:" + brandId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
