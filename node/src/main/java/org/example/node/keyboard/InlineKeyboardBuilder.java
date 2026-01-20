package org.example.node.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class InlineKeyboardBuilder {

    public <T>InlineKeyboardMarkup buildListKeyboard(
            List<T> items,
            Function<T, String> textExtractor,
            Function<T, String> dataExtractor,
            int buttonsPerRow,
            String backData) {

        var keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);

            var button = new InlineKeyboardButton();
            button.setText(textExtractor.apply(item));
            button.setCallbackData(dataExtractor.apply(item));

            row.add(button);
            if (row.size() == buttonsPerRow || i == items.size() - 1) {
                keyboard.add(new ArrayList<>(row));
                row.clear();
            }
        }

        if (backData != null) {
            keyboard.add(List.of(createBackButton(backData)));
        }
        keyboard.add(List.of(crateHomeButton()));
        
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardButton createBackButton(String backData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("◀️ Назад");
        button.setCallbackData("back:" + backData);
        return button;
    }

    private InlineKeyboardButton crateHomeButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83C\uDFE0 Главное меню");
        button.setCallbackData("main_menu:main_menu");
        return button;
    }
}
