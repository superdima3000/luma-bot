package org.example.node.service;

import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupSentDto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.awt.*;

public interface ConsumerService {
    void consumeTextMessageUpdates(Update update);
    void consumeDocMessageUpdates(Update update);
    void consumePhotoMessageUpdates(Update update);
    void consumeCallbackQueueUpdates(Update update);
    void consumeMediaGroupSent(MediaGroupSentDto dto);
    void consumeImageDownloaded(ImageFileDto dto);

}
