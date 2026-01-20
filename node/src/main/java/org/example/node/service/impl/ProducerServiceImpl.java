package org.example.node.service.impl;

import jakarta.validation.OverridesAttribute;
import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupDto;
import org.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
@Slf4j
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceAnswerCallback(AnswerCallbackQuery answerCallbackQuery) {
        rabbitTemplate.convertAndSend(ANSWER_CALLBACK_QUEUE, answerCallbackQuery);
    }

    @Override
    public void produceDeleteMessage(DeleteMessage deleteMessage) {
        rabbitTemplate.convertAndSend(DELETE_MESSAGE_QUEUE, deleteMessage);
    }

    @Override
    public void produceEditMessage(EditMessageText editMessage){
        rabbitTemplate.convertAndSend(EDIT_MESSAGE_QUEUE, editMessage);
    }

    @Override
    public void produceEditMarkup(EditMessageReplyMarkup editMessageReplyMarkup){
        rabbitTemplate.convertAndSend(EDIT_MARKUP_QUEUE, editMessageReplyMarkup);
    }

    @Override
    public void produceMediaGroup(MediaGroupDto sendMediaGroup) {
        rabbitTemplate.convertAndSend(ANSWER_MEDIA_GROUP, sendMediaGroup);
    }

    @Override
    public void producePhotoDownload(ImageFileDto imageFileDto) {
        rabbitTemplate.convertAndSend(DOWNLOAD_FILE_QUEUE, imageFileDto);
    }

    @Override
    public void producePhoto(SendPhoto sendPhoto) {
        rabbitTemplate.convertAndSend(ANSWER_PHOTO_QUEUE, sendPhoto);
    }
}
