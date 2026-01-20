package org.example.node.service;

import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;
import java.util.function.Consumer;

public interface FileService {
    void handleMediaGroupPhoto(String mediaGroupId, PhotoSize largestPhoto, Consumer<List<String>> callback);
    void downloadSinglePhoto(PhotoSize photo, boolean edit, Long chatId, Long userId);
    void processMediaGroup(String mediaGroupId);
}
