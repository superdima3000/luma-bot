package org.example.node.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.http.model.ImageModel;
import org.example.http.model.dto.MediaGroupDto;
import org.example.node.keyboard.StaticKeyboard;
import org.example.node.service.ProducerService;
import org.example.node.service.SenderService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SenderServiceImpl implements SenderService {

    private final ProducerService producerService;

    @Override
    public void sendAnswer(Long chatId, String output) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    @Override
    public void deleteMessage(Message message) {
        var deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        producerService.produceDeleteMessage(deleteMessage);
    }

    @Override
    public void deleteMessage(Long chatId, Integer messageId) {
        var deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        producerService.produceDeleteMessage(deleteMessage);
    }

    @Override
    public void sendAnswerWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboard);
        sendMessage.setParseMode("HTML");
        log.debug("sending answer to chat {}", chatId);
        producerService.produceAnswer(sendMessage);
    }

    @Override
    public void sendAnswerWithPhoto(Long chatId, String text, InlineKeyboardMarkup keyboard, String photo){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new java.io.File(photo)));
        sendPhoto.setCaption(text);
        sendPhoto.setReplyMarkup(keyboard);
        sendPhoto.setParseMode("HTML");
        log.debug("sending photo answer to chat {}", chatId);
        producerService.producePhoto(sendPhoto);
    }

    @Override
    public void sendMediaGroup(Long chatId, List<ImageModel> images, InlineKeyboardMarkup keyboard, Long itemId, String itemInfo){
        List<String> names = new ArrayList<>();
        for (ImageModel image : images) {
            names.add(image.getImage());
        }

        MediaGroupDto mediaGroupDto = MediaGroupDto.builder()
                .chatId(chatId)
                .itemId(itemId)
                .images(names)
                .keyboard(keyboard)
                .itemInfo(itemInfo)
                .build();

        producerService.produceMediaGroup(mediaGroupDto);
    }
}
