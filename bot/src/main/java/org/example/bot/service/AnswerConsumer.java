package org.example.bot.service;

import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupDto;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

public interface AnswerConsumer {
    void consume(SendMessage sendMessage);
    void consumeCallback(AnswerCallbackQuery answerCallbackQuery);
    void consumeDelete(DeleteMessage deleteMessage);
    void consumeEdit(EditMessageText editMessage);
    void consumeEditMarkup(EditMessageReplyMarkup editMessageReplyMarkup);
    void consumeMediaGroup(MediaGroupDto sendMediaGroup);
    void consumeDownloadFile(ImageFileDto imageFileDto);
    void consumeAnswerPhoto(SendPhoto sendPhoto);
}
