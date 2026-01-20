package org.example.node.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {
    boolean canHandle(String callbackData);
    void handle(CallbackQuery callbackQuery);
    default int getOrder() {
        return 100;
    }
}
