package org.example.node.service;

import org.example.http.model.ImageModel;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface SenderService {
    void deleteMessage(Message message);
    void sendAnswer(Long chatId, String answer);
    void sendAnswerWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard);
    void sendAnswerWithPhoto(Long chatId, String text, InlineKeyboardMarkup keyboard, String photo);
    void deleteMessage(Long chatId, Integer messageId);
    void sendMediaGroup(Long chatId, List<ImageModel> images, InlineKeyboardMarkup keyboard, Long itemId, String itemInfo);
}
