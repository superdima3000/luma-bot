package org.example.http.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaGroupSentDto {
    Long itemId;
    Long chatId;
    String itemInfo;
    InlineKeyboardMarkup keyboard;
    Integer amount;
    Integer messageId;
}
