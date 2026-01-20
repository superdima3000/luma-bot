package org.example.bot.service;

import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupSentDto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.awt.*;

public interface UpdateProducer {
    void produce(Update update, String rabbitQueue);
    void produceMediaGroupSent(MediaGroupSentDto dto);
    void produceImageDownloaded(ImageFileDto imageFileDto);
}
