package org.example.node.service;

import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupDto;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.awt.*;
import java.util.List;

public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
    void produceAnswerCallback(AnswerCallbackQuery answerCallbackQuery);
    void produceDeleteMessage(DeleteMessage sendMessage);
    void produceEditMessage(EditMessageText editMessage);
    void produceEditMarkup(EditMessageReplyMarkup editMessageReplyMarkup);
    void produceMediaGroup(MediaGroupDto sendMediaGroup);
    void producePhotoDownload(ImageFileDto imageFileDto);
    void producePhoto(SendPhoto sendPhoto);
}
