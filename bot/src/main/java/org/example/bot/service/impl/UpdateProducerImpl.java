package org.example.bot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.service.UpdateProducer;
import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupSentDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.myApp.model.RabbitQueue.IMAGE_DOWNLOADED_QUEUE;
import static org.example.myApp.model.RabbitQueue.MEDIA_GROUP_SENT;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateProducerImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(Update update, String rabbitQueue) {
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }

    @Override
    public void produceMediaGroupSent(MediaGroupSentDto dto){
        rabbitTemplate.convertAndSend(MEDIA_GROUP_SENT, dto);
    }

    @Override
    public void produceImageDownloaded(ImageFileDto imageFileDto) {
        rabbitTemplate.convertAndSend(IMAGE_DOWNLOADED_QUEUE, imageFileDto);
    }
}
