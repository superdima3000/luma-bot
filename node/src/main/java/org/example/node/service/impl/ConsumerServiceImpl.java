package org.example.node.service.impl;

import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupSentDto;
import org.example.node.service.ConsumerService;
import org.example.node.service.MainService;
import org.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.myApp.model.RabbitQueue.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;
    private final MainService mainService;

    @Override
    @RabbitListener(queues = TEXT_QUEUE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Consuming text message update");

        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = CALLBACK_QUEUE_UPDATE)
    public void consumeCallbackQueueUpdates(Update update) {
        log.debug("NODE: Consuming callback queue update");

        mainService.processCallbackQuery(update);
    }

    @Override
    @RabbitListener(queues = DOC_QUEUE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Consuming doc message update");
    }

    @Override
    @RabbitListener(queues = PHOTO_QUEUE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        mainService.processPhotoMessage(update);
    }

    @Override
    @RabbitListener(queues = MEDIA_GROUP_SENT)
    public void consumeMediaGroupSent(MediaGroupSentDto dto) {
        mainService.processMediaGroupSent(dto);
    }

    @Override
    @RabbitListener(queues = IMAGE_DOWNLOADED_QUEUE)
    public void consumeImageDownloaded(ImageFileDto dto) {
        mainService.processImageDownloaded(dto);
    }
}
