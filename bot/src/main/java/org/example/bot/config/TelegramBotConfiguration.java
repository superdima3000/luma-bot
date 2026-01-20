package org.example.bot.config;

import org.example.bot.bot.LumaBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(LumaBot lumaBot) {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(lumaBot);  // ВОТ ЭТО ВАЖНО!
            System.out.println("Бот успешно зарегистрирован!");
            return api;
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка регистрации бота: " + e.getMessage(), e);
        }
    }
}
