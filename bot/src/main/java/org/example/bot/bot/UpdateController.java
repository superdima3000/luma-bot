package org.example.bot.bot;

import ch.qos.logback.core.encoder.EchoEncoder;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.service.UpdateProducer;
import org.example.bot.util.MessageUtil;
import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupDto;
import org.example.http.model.dto.MediaGroupSentDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.example.myApp.model.RabbitQueue.*;

@Component
@Slf4j
public class UpdateController {
    private LumaBot lumaBot;

    private final MessageUtil messageUtil;
    private final UpdateProducer updateProducer;


    public UpdateController(MessageUtil messageUtil, UpdateProducer updateProducer) {
        this.messageUtil = messageUtil;
        this.updateProducer = updateProducer;
    }
    public void registerBot(LumaBot lumaBot) {
        this.lumaBot = lumaBot;
    }

    public void processUpdate(Update update) {
        if (update == null){
            log.error("Update is null");
            return;
        }

        if (update.getMessage() != null){
            distributeMessagesByType(update);
        }
        else if (update.hasCallbackQuery()) {
            processCallbackQuery(update);
        }
        else{
            log.error("Update is unsupported");
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();

        if(message.getText() != null){
            processTextMessage(update);
        } else if (message.getDocument() != null){
            processDocMessage(update);
        } else if (message.getPhoto() != null){
            processPhotoMessage(update);
        } else {
            setUnsupportedTypeView(update);
        }
    }

    private void setUnsupportedTypeView(Update update) {
        var sendMessage = messageUtil.generateMessageWithText(update,
                "Сообщение не поддерживается");
        setView(sendMessage);
    }

    public void setAnswerCallback(AnswerCallbackQuery answerCallbackQuery) {
        lumaBot.answerCallbackQuery(answerCallbackQuery);
    }

    public void setView(SendMessage sendMessage) {
        lumaBot.sendMessage(sendMessage);
    }

    public void setDelete(DeleteMessage deleteMessage) {
        lumaBot.deleteMessage(deleteMessage);
    }

    public void setEdit(EditMessageText editMessage) {
        lumaBot.editMessage(editMessage);
    }

    public void setEditMarkup(EditMessageReplyMarkup editMessageReplyMarkup) {
        lumaBot.editMessageReplyMarkup(editMessageReplyMarkup);
    }

    public void setPhotoView(SendPhoto sendPhoto){
        lumaBot.sendPhoto(sendPhoto);
    }

    public void setMediaGroupView(MediaGroupDto mediaList){
        if (mediaList.getImages().size() == 1){
            var sendPhoto = transformSendPhoto(mediaList);
            lumaBot.sendPhoto(sendPhoto);
        } else {
            var sendMediaGroup = transformMediaGroup(mediaList);
            lumaBot.sendMediaGroup(sendMediaGroup, mediaList.getItemId(), mediaList.getChatId(),
                    mediaList.getItemInfo(), mediaList.getKeyboard());
        }
    }

    private SendPhoto transformSendPhoto(MediaGroupDto mediaList) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(mediaList.getChatId());
        sendPhoto.setPhoto(new InputFile(new java.io.File(mediaList.getImages().getFirst())));
        sendPhoto.setCaption(mediaList.getItemInfo());
        sendPhoto.setReplyMarkup(mediaList.getKeyboard());
        sendPhoto.setParseMode("HTML");
        return sendPhoto;
    }

    public void sendMediaGroupDto(List<Message> messages, Long chatId, Long itemId,
                                  String itemInfo, InlineKeyboardMarkup keyboard){
        Integer mediaGroupFirstMessageId = messages.getFirst().getMessageId();
        var mediaGroupSentDto = MediaGroupSentDto.builder()
                .chatId(chatId)
                .itemId(itemId)
                .itemInfo(itemInfo)
                .keyboard(keyboard)
                .amount(messages.size())
                .messageId(mediaGroupFirstMessageId)
                .build();

        updateProducer.produceMediaGroupSent(mediaGroupSentDto);
    }

    public void sendImageDownloadedDto(ImageFileDto imageFileDto){
        updateProducer.produceImageDownloaded(imageFileDto);
    }

    private SendMediaGroup transformMediaGroup(MediaGroupDto images) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();

        List<InputMedia> mediaList = new ArrayList<>();

        for (var image : images.getImages()) {
            InputMedia input = new InputMediaPhoto();
            File photoFile = new File(image);
            input.setMedia(photoFile, image);
            mediaList.add(input);
        }

        sendMediaGroup.setChatId(images.getChatId());
        sendMediaGroup.setMedias(mediaList);
        return sendMediaGroup;
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(update, PHOTO_QUEUE_UPDATE);
        //setFileIsReceivedView(update);
    }

    public void downloadFile(ImageFileDto imageFileDto) {
        lumaBot.fileDownload(imageFileDto);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtil.generateMessageWithText(update,
                "Файл обрабатывается");
        setView(sendMessage);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(update, DOC_QUEUE_UPDATE);
        setFileIsReceivedView(update);
    }

    private void processCallbackQuery(Update update) {
        updateProducer.produce(update, CALLBACK_QUEUE_UPDATE);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(update, TEXT_QUEUE_UPDATE);
    }
}
