package org.example.http.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.GetFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageFileDto {
    GetFile getFile;
    String mediaGroupId;
    String filePath;
    Long chatId;
    Long userId;
    boolean edit;
}
