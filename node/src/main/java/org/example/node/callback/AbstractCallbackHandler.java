package org.example.node.callback;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonjpa.entity.AppUser;
import org.example.commonjpa.repository.AppUserRepository;
import org.example.commonjpa.repository.MediaGroupRepository;
import org.example.http.model.ImageModel;
import org.example.http.model.ItemModel;
import org.example.http.model.dto.MediaGroupDto;
import org.example.http.service.ClientService;
import org.example.node.keyboard.InlineKeyboardBuilder;
import org.example.node.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractCallbackHandler implements CallbackHandler {
    protected abstract String getPrefix();

    @Autowired
    private ProducerService producerService;

    @Autowired
    protected AdminService adminService;

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected InlineKeyboardBuilder inlineKeyboardBuilder;

    @Autowired
    protected SenderService senderService;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected AppUserRepository appUserRepository;

    @Override
    public boolean canHandle(String callbackData) {
        return callbackData != null && callbackData.startsWith(getPrefix());
    }

    protected Long extractId(String callbackData) {
        String[] parts = callbackData.split(":");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid callback data format: " + callbackData);
        }
        return Long.parseLong(parts[1]);
    }

    protected void answerCallbackQuery(String callbackQueryId, String text, boolean showAlert) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        answer.setText(text);
        answer.setShowAlert(showAlert);
        producerService.produceAnswerCallback(answer);
    }

    protected void answerCallbackQuery(String callbackQueryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        producerService.produceAnswerCallback(answer);
    }

    protected void editMessageText(Long chatId, Integer messageId, String text) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode("HTML");
        producerService.produceEditMessage(editMessage);
    }

    protected void editMessageKeyboard(Long chatId, Integer messageId, InlineKeyboardMarkup keyboard) {
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(chatId);
        editMarkup.setMessageId(messageId);
        editMarkup.setReplyMarkup(keyboard);
        producerService.produceEditMarkup(editMarkup);
    }

    protected void editMessageTextAndKeyboard(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        senderService.deleteMessage(chatId, messageId);
        senderService.sendAnswerWithKeyboard(chatId, text, keyboard);
    }

    protected void editMessageTextMediaAndKeyboard(Long chatId,
                                                   Integer messageId,
                                                   InlineKeyboardMarkup keyboard,
                                                   String itemInfo,
                                                   List<ImageModel> images,
                                                   Long itemId) {
        senderService.deleteMessage(chatId, messageId);

        senderService.sendMediaGroup(chatId, images, keyboard, itemId, itemInfo);
    }

    protected void generateNewKeyboard(List<ItemModel> items,
                                       String queryId,
                                       Long chatId,
                                       Integer messageId,
                                       String text,
                                       String callbackData) {
        if (items.isEmpty()) {
            answerCallbackQuery(queryId,
                    "Товары не найдены",
                    true);
            return;
        }

        var keyboard = inlineKeyboardBuilder.buildListKeyboard(
                items,
                ItemModel::getName,
                i -> "item:" + i.getId() + ":" + callbackData,
                2,
                callbackData
        );

        editMessageTextAndKeyboard(
                chatId,
                messageId,
                text,
                keyboard
        );
        answerCallbackQuery(queryId);
    }
}
