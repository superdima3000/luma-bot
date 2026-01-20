package org.example.node.callback.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.commonjpa.entity.enums.Role;
import org.example.node.callback.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@Slf4j
public class MainMenuCallbackHandler extends AbstractCallbackHandler {

    @Override
    protected String getPrefix(){
        return "main_menu:";
    }

    @Override
    public boolean canHandle(String callbackData){
        return callbackData.startsWith(getPrefix());
    }

    @Override
    public void handle(CallbackQuery callbackQuery){
        Long chatId = callbackQuery.getMessage().getChatId();
        Long userId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String data = callbackQuery.getData();
        var parts = data.split(":");
        String destination = parts[1];
        switch (destination){
            case "main_menu" -> {
                if (parts.length == 3){
                    var itemId = Long.parseLong(parts[2]);
                    menuService.tryDeletingMediaGroup(chatId, itemId);
                }

                showMainMenu(chatId, messageId, userId);

            }
            case "brands" -> {
                menuService.showBrandsList(chatId, messageId);
            }
            case "categories" -> {
                menuService.showCategoriesList(chatId, messageId);
            }
            case "stock" -> {
                menuService.showItemsList(chatId, messageId);
            }
        }
        senderService.deleteMessage(callbackQuery.getMessage());

    }

    private void showMainMenu(Long chatId, Integer messageId, Long userId){
        log.debug("User ID: {}", userId);
        var appUser = appUserRepository.findByUserId(userId);
        if (appUser != null){
            menuService.showMainMenu(chatId, messageId, Role.ADMIN.equals(appUser.getRole()));
        }

    }
}
