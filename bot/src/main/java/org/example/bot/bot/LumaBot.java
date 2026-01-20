package org.example.bot.bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.http.model.dto.ImageFileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class LumaBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    private UpdateController updateController;

    public LumaBot(@Value("${telegram.bot.token}") String botToken,
                   UpdateController updateController) {
        super(botToken);
        this.updateController = updateController;
    }

    @PostConstruct
    public void init(){
        this.updateController.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void deleteMessage(DeleteMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void answerCallbackQuery(AnswerCallbackQuery answerCallbackQuery) {
        if (answerCallbackQuery != null) {
            try {
                execute(answerCallbackQuery);
            } catch (TelegramApiException e) {
                log.error("Failed to answer callback query {}: {}",
                        answerCallbackQuery.getCallbackQueryId(), e.getMessage());
            }
        }
    }

    public void editMessage(EditMessageText editMessage){
        if (editMessage != null) {
            try {
                execute(editMessage);
            } catch (TelegramApiException e) {
                log.error("Failed to edit message {}", e.getMessage());
            }
        }
    }

    public void editMessageReplyMarkup(EditMessageReplyMarkup replyMarkup){
        if (replyMarkup != null) {
            try {
                execute(replyMarkup);
            } catch (TelegramApiException e) {
                log.error("Failed to edit message reply markup {}", e.getMessage());
            }
        }
    }

    public void sendMediaGroup(SendMediaGroup sendMediaGroup, Long itemId, Long chatId,
                               String itemInfo, InlineKeyboardMarkup inlineKeyboardMarkup) {
        if (sendMediaGroup != null) {
            try {
                List<Message> messages = execute(sendMediaGroup);
                updateController.sendMediaGroupDto(messages, chatId, itemId, itemInfo, inlineKeyboardMarkup);
            } catch (TelegramApiException e) {
                log.error("Failed to send media group {}", e);
            }
        }
    }

    public void fileDownload(ImageFileDto imageFileDto){
        if (imageFileDto != null) {
            try {
                var telegramFile = execute(imageFileDto.getGetFile());

                File outputFile = new File(imageFileDto.getFilePath());
                outputFile.getParentFile().mkdirs();

                File downloadedFile = downloadFile(telegramFile, outputFile);
                System.out.println("Фото сохранено: " + downloadedFile.getAbsolutePath());
                log.debug("Edit: {}", imageFileDto.isEdit());
                updateController.sendImageDownloadedDto(imageFileDto);
            } catch (TelegramApiException e) {
                log.error("Failed to download file {}", e.getMessage());
            }
        }
    }

    public void sendPhoto(SendPhoto sendPhoto){
        if (sendPhoto != null) {
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                log.error("Failed to send photo{}", e.getMessage());
            }
        }
    }
}
