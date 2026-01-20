package org.example.node.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.http.model.dto.ImageFileDto;
import org.example.node.service.FileService;
import org.example.node.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import javax.xml.transform.sax.SAXResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final ProducerService producerService;

    private final Map<String, List<PhotoSize>> mediaGroups = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> groupTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<String, Consumer<List<String>>> callbacks = new ConcurrentHashMap<>();


    @Override
    public void handleMediaGroupPhoto(String mediaGroupId, PhotoSize photo, Consumer<List<String>> callback) {

        callbacks.put(mediaGroupId, callback);

        mediaGroups.computeIfAbsent(mediaGroupId, k -> new ArrayList<>()).add(photo);
        ScheduledFuture<?> existingTimer = groupTimers.get(mediaGroupId);

        if (existingTimer != null) {
            existingTimer.cancel(false);
        }

        ScheduledFuture<?> newTimer = scheduler.schedule(() -> {
            processMediaGroup(mediaGroupId);
        }, 1, TimeUnit.SECONDS);

        groupTimers.put(mediaGroupId, newTimer);
    }

    public void processMediaGroup(String mediaGroupId) {
        List<PhotoSize> photos = mediaGroups.remove(mediaGroupId);
        groupTimers.remove(mediaGroupId);

        List<String> groupFileNames = new ArrayList<>();

        if (photos == null || photos.isEmpty()) {
            return;
        }

        for (int i = 0; i < photos.size(); i++) {
            PhotoSize photo = photos.get(i);
            String fileName = "images/img_group_" + mediaGroupId + "_photo_" + (i + 1) + ".jpg";
            downloadPhoto(photo, fileName, mediaGroupId, false, null, null);
            groupFileNames.add(fileName);
        }


        Consumer<List<String>> callback = callbacks.remove(mediaGroupId);
        if (callback != null) {
            log.debug("Accepting callback for fileGroup {}", groupFileNames);
            callback.accept(groupFileNames);
        }
    }

    @Override
    public void downloadSinglePhoto(PhotoSize photo, boolean edit, Long chatId, Long userId) {
        System.out.println("Скачивание одиночного фото");
        String fileName = "images/img_" + photo.getFileId() + ".jpg";
        log.debug("Edit: {}", edit);
        downloadPhoto(photo, fileName, null, edit, chatId, userId);
    }

    private void downloadPhoto(PhotoSize photo, String fileName, String mediaGroupId, boolean edit, Long chatId, Long userId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(photo.getFileId());

        log.debug("Edit 2: {}", edit);

        var imageFileDto = ImageFileDto.builder()
                        .filePath(fileName)
                                .getFile(getFile)
                                        .mediaGroupId(mediaGroupId)
                .edit(edit)
                .chatId(chatId)
                .userId(userId)
                                        .build();

        producerService.producePhotoDownload(imageFileDto);
    }

}
