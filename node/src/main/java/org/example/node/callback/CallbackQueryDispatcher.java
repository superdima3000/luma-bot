package org.example.node.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class CallbackQueryDispatcher {

    private final List<CallbackHandler> handlers;

    public CallbackQueryDispatcher(List<CallbackHandler> handlers) {
        this.handlers = handlers.stream()
                .sorted(Comparator.comparingInt(CallbackHandler::getOrder))
                .toList();

        log.info("Registered {} callback handlers", handlers.size());
    }

    public void dispatch(final CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        log.debug("Received callback query: {}", data);
        for (CallbackHandler handler : handlers) {
            if (handler.canHandle(data)) {
                handler.handle(callbackQuery);
                return;
            }
        }
    }
}
