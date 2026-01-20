package org.example.http.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaGroupDto {
    Long chatId;
    Long itemId;
    String itemInfo;
    InlineKeyboardMarkup keyboard;
    List<String> images;
}
