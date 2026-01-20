package org.example.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.bot.bot.UpdateController;
import org.example.bot.service.AnswerConsumer;
import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;

import static org.example.myApp.model.RabbitQueue.*;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = ANSWER_CALLBACK_QUEUE)
    public void consumeCallback(AnswerCallbackQuery answerCallbackQuery) {
        updateController.setAnswerCallback(answerCallbackQuery);
    }

    @Override
    @RabbitListener(queues = DELETE_MESSAGE_QUEUE)
    public void consumeDelete(DeleteMessage deleteMessage) {
        updateController.setDelete(deleteMessage);
    }

    @Override
    @RabbitListener(queues = EDIT_MESSAGE_QUEUE)
    public void consumeEdit(EditMessageText editMessage){
        updateController.setEdit(editMessage);
    }

    @Override
    @RabbitListener(queues = EDIT_MARKUP_QUEUE)
    public void consumeEditMarkup(EditMessageReplyMarkup editMessageReplyMarkup){
        updateController.setEditMarkup(editMessageReplyMarkup);
    }

    @Override
    @RabbitListener(queues = ANSWER_MEDIA_GROUP)
    public void consumeMediaGroup(MediaGroupDto sendMediaGroup) {
        updateController.setMediaGroupView(sendMediaGroup);
    }

    @Override
    @RabbitListener(queues = DOWNLOAD_FILE_QUEUE)
    public void consumeDownloadFile(ImageFileDto imageFileDto) {
        updateController.downloadFile(imageFileDto);
    }

    @Override
    @RabbitListener(queues = ANSWER_PHOTO_QUEUE)
    public void consumeAnswerPhoto(SendPhoto sendPhoto) {
        updateController.setPhotoView(sendPhoto);
    }
}
