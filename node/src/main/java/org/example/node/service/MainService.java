package org.example.node.service;

import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupSentDto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.awt.*;

public interface MainService {
    void processTextMessage(Update update);
    void processCallbackQuery(Update update);
    void processPhotoMessage(Update update);
    void processMediaGroupSent(MediaGroupSentDto dto);
    void processImageDownloaded(ImageFileDto dto);
}
