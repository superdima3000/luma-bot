package org.example.bot.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.example.myApp.model.RabbitQueue.*;

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(TEXT_QUEUE_UPDATE);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(DOC_QUEUE_UPDATE);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(PHOTO_QUEUE_UPDATE);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(ANSWER_MESSAGE);
    }

    @Bean
    public Queue deleteMessageQueue() {
        return new Queue(DELETE_MESSAGE_QUEUE);
    }

    @Bean
    public Queue callbackUpdateQueue() { return new Queue(CALLBACK_QUEUE_UPDATE); }

    @Bean
    public Queue answerCallbackQueue() { return new Queue(ANSWER_CALLBACK_QUEUE); }

    @Bean
    public Queue editMessageQueue() { return new Queue(EDIT_MESSAGE_QUEUE); }

    @Bean
    public Queue editMessageMarkup() { return new Queue(EDIT_MARKUP_QUEUE); }

    @Bean
    public Queue answerMediaGroupQueue() { return new Queue(ANSWER_MEDIA_GROUP); }

    @Bean
    public Queue mediaGroupSentQueue() { return new Queue(MEDIA_GROUP_SENT); }

    @Bean
    public Queue downloadFileQueue() { return new Queue(DOWNLOAD_FILE_QUEUE); }

    @Bean
    public Queue imageDownloadedQueue() { return new Queue(IMAGE_DOWNLOADED_QUEUE); }

    @Bean
    public Queue answerPhotoQueue() { return new Queue(ANSWER_PHOTO_QUEUE); }
}
