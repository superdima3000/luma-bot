package org.example.node.callback.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.example.http.model.ImageModel;
import org.example.http.model.ItemModel;
import org.example.node.callback.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ItemCallbackHandler extends AbstractCallbackHandler {

    @Override
    protected String getPrefix() {
        return "item:";
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        String[] parts = data.split(":");
        Long itemId = Long.parseLong(parts[1]);
        String sourceType = parts[2];
        Long sourceId = Long.parseLong(parts[3]);
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        try {
            var item = clientService.getObject("http://localhost:8082/api/items/" + itemId, ItemModel.class);
            log.debug("http://localhost:8082/api/items/" + itemId);
            List<ImageModel> images = clientService.getList("http://localhost:8082/api/items/" + itemId + "/images", ImageModel.class);
            log.debug("http://localhost:8082/api/items/" + itemId + "/images");
            String itemInfo = formatItemInfo(item);

            var keyboard = createItemKeyboard(sourceType, sourceId, itemId);

            if (images.isEmpty()) {
                editMessageTextAndKeyboard(chatId, messageId, itemInfo, keyboard);
            } else {
                log.debug("Adding image to keyboard");
                editMessageTextMediaAndKeyboard(chatId, messageId, keyboard, itemInfo, images, itemId);
            }
            answerCallbackQuery(callbackQuery.getId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private String formatItemInfo(ItemModel item) {
        StringBuilder info = new StringBuilder();
        info.append("<b>").append(item.getName()).append("</b>\n\n");

        if (item.getDescription() != null) {
            info.append(item.getDescription()).append("\n\n");
        }

        if (item.getPrice() != null) {
            info.append("💰 Цена: ").append(item.getPrice()).append(" руб.\n");
        }

        if (item.getSizes() != null && !item.getSizes().isEmpty()) {
            StringBuilder sizesBuilder = new StringBuilder();
            for (var size : item.getSizes()) {
                if (!sizesBuilder.isEmpty()) {
                    sizesBuilder.append(", ");
                }
                sizesBuilder.append(size.getName());
            }
            info.append("\uD83D\uDD20 Размеры: ").append(sizesBuilder).append("\n");
        }

        return info.toString();
    }

    private InlineKeyboardMarkup createItemKeyboard(String sourceType, Long sourceId, Long itemId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText("◀️ Назад");
        backBtn.setCallbackData("back:items:" + itemId + ":" + sourceType + ":" + sourceId);

        InlineKeyboardButton mainMenu = new InlineKeyboardButton();
        mainMenu.setText("\uD83C\uDFE0 Главное меню");
        mainMenu.setCallbackData("main_menu:main_menu:"+itemId);

        rows.add(List.of(backBtn));
        rows.add(List.of(mainMenu));
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
