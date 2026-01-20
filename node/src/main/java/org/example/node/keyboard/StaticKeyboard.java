package org.example.node.keyboard;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.node.keyboard.KeyboardConstants.*;

@Component
@Getter
public class StaticKeyboard {
    private ReplyKeyboardMarkup replyKeyboardMarkup;

    public StaticKeyboard() {
        this.replyKeyboardMarkup = createReplyKeyboardMarkup();
    }

    private ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        KeyboardRow row1 = new KeyboardRow();

        row1.add(new KeyboardButton(SEARCH_BY_STOCK));
        row1.add(new KeyboardButton(SEARCH_BY_CATEGORY));

        KeyboardRow row2 = new KeyboardRow();

        row2.add(new KeyboardButton(SEARCH_BY_BRAND));
        row2.add(new KeyboardButton(MAIN_MENU));

        List<KeyboardRow> rows = Arrays.asList(row1, row2);
        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(false)
                .build();
    }
}
