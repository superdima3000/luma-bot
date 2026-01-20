package org.example.node.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonjpa.entity.ImageDraft;
import org.example.commonjpa.entity.MediaGroupModel;
import org.example.commonjpa.entity.dto.ImageDraftCreateDto;
import org.example.commonjpa.entity.dto.ItemDraftCreateDto;
import org.example.commonjpa.repository.MediaGroupRepository;
import org.example.commonjpa.service.ItemDraftService;
import org.example.http.model.*;
import org.example.http.model.dto.MediaGroupDto;
import org.example.http.service.ClientService;
import org.example.node.keyboard.InlineKeyboardBuilder;
import org.example.node.service.MenuService;
import org.example.node.service.ProducerService;
import org.example.node.service.SenderService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final ClientService clientService;
    private final InlineKeyboardBuilder inlineKeyboardBuilder;
    private final SenderService senderService;
    private final ProducerService producerService;
    private final MediaGroupRepository mediaGroupRepository;
    private final ItemDraftService itemDraftService;

    @Override
    public void showBrandsList(Long chatId, Integer messageId){
        try {
            log.debug("showBrandsList");
            var brands = clientService.getList("http://localhost:8082/api/brands?quantity=true", BrandModel.class);
            var keyboard = inlineKeyboardBuilder.buildListKeyboard(
                    brands,
                    BrandModel::getName,
                    b -> "brands:" + b.getId(),
                    2,
                    null
            );
            senderService.sendAnswerWithKeyboard(chatId, "Выберите бренд:", keyboard);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void showCategoriesList(Long chatId, Integer messageId){
        try {
            var categories = clientService.getList("http://localhost:8082/api/categories?quantity=true", CategoryModel.class);
            var keyboard = inlineKeyboardBuilder.buildListKeyboard(
                    categories,
                    CategoryModel::getName,
                    c -> "category:" + c.getId(),
                    2,
                    null
            );
            senderService.sendAnswerWithKeyboard(chatId, "Выберите категорию:", keyboard);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void showItemsList(Long chatId, Integer messageId){
        log.debug("showItemsList");
        try {
            var items = clientService.getList("http://localhost:8082/api/items?quantity=1", ItemModel.class);
            var keyboard = inlineKeyboardBuilder.buildListKeyboard(
                    items,
                    ItemModel::getName,
                    i -> "item:" + i.getId() + ":stock:0",
                    2,
                    null
            );
            senderService.sendAnswerWithKeyboard(chatId, "Товары в наличии: ", keyboard);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void showMainMenu(Long chatId, Integer messageId, boolean isAdmin){
        var keyboard = createMainMenuKeyboard(isAdmin);
        senderService.sendAnswerWithKeyboard(chatId, "Добро пожаловать в luma.bot", keyboard);
    }


    @Override
    public void tryDeletingMediaGroup(Long chatId, Long itemId) {
        Optional<MediaGroupModel> optionalMediaGroupModel = mediaGroupRepository.findTop1ByChatIdAndItemIdOrderByCreatedDesc(
                chatId, itemId
        );
        if (optionalMediaGroupModel.isPresent()) {
            var mediaGroup = optionalMediaGroupModel.get();
            int messageId = mediaGroup.getMessageId();
            for (int i = 0; i < mediaGroup.getAmount(); i++) {
                senderService.deleteMessage(chatId, messageId + i);
            }
            mediaGroupRepository.delete(mediaGroup);
        }
    }

    @Override
    public void showAdminMenu(Long chatId, Integer messageId) {
        var keyboard = createAdminKeyboard();
        senderService.sendAnswerWithKeyboard(chatId, "Ассаламу алейкум адмiн", keyboard);
    }

    @Override
    public void showItemsForName(Long chatId, Integer messageId, String text) {
        try {
            var items = clientService.getList("http://localhost:8082/api/items?name=" + text, ItemModel.class);
            var keyboard = inlineKeyboardBuilder.buildListKeyboard(
                    items,
                    ItemModel::getName,
                    i -> "admin_menu:item:" + i.getId(),
                    2,
                    null
            );
            senderService.sendAnswerWithKeyboard(chatId, "Товары по запросу " + text + ": ", keyboard);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void showItemEditMenu(Long chatId, ItemDraftCreateDto draft) {
        var keyboardMarkup = createEditItemKeyboard(draft.getId());
        var text = formatItemInfo(draft);
        var images = itemDraftService.getItemImages(draft.getId());
        if (!images.isEmpty()) {
            List<String> names = new ArrayList<>();

            for (var image : images) {
                names.add(image.getImage());
            }

            MediaGroupDto mediaGroupDto = MediaGroupDto.builder()
                    .chatId(chatId)
                    .itemId(draft.getId())
                    .images(names)
                    .keyboard(keyboardMarkup)
                    .itemInfo(text)
                    .build();

            producerService.produceMediaGroup(mediaGroupDto);
        } else {
            senderService.sendAnswerWithKeyboard(chatId, text, keyboardMarkup);
        }
    }

    @Override
    public void showEditSizeMenu(Long chatId, ItemDraftCreateDto draft) {
        var sizes = itemDraftService.getItemSizes(draft.getId());

        var text = "Размеры вещи: " + String.join(", ", sizes);
        var keyboard = createEditSizesKeyboard(draft.getId());

        senderService.sendAnswerWithKeyboard(chatId, text, keyboard);
    }

    @Override
    public void showEditSizeRemoveMenu(Long chatId, ItemDraftCreateDto draft) {
        var sizes = itemDraftService.getItemSizes(draft.getId());
        var text = "Выберите размер для удаления: ";

        var keyboard = inlineKeyboardBuilder.buildListKeyboard(
                sizes,
                size -> size,
                size -> "admin_menu:delete_size:" + size,
                5,
                null
        );

        senderService.sendAnswerWithKeyboard(chatId, text, keyboard);
    }

    @Override
    public void showItemEditImagesMenu(Long chatId, ItemDraftCreateDto draft) {
        var imagesDto = itemDraftService.getItemImages(draft.getId());

        String itemInfo = "Редактор изображений";
        var images = imagesDto.stream()
                .map(i -> ImageModel.builder().image(i.getImage()).build())
                .toList();
        log.debug("received image list: {}", images);
        var keyboard = createEditImagesKeyboard(draft.getId());

        if (images != null && !images.isEmpty()) {
            senderService.sendMediaGroup(chatId, images, keyboard, draft.getId(), itemInfo);
        } else {
            senderService.sendAnswerWithKeyboard(chatId, itemInfo, keyboard);
        }
    }

    @Override
    public void showEditImagesRemoveMenu(Long chatId, ItemDraftCreateDto draft) {
        var imagesDto = itemDraftService.getItemImages(draft.getId());
        String itemInfo = "Выберите изображение для удаления: ";
        var images = imagesDto.stream()
                .map(i -> ImageModel.builder().image(i.getImage()).build())
                .toList();

        var keyboard = createEditImagesRemoveKeyboard(imagesDto, draft.getId());
        senderService.sendMediaGroup(chatId, images, keyboard, draft.getId(), itemInfo);
    }

    @Override
    public void showAllItems(Long chatId, Integer messageId) {
        try {
            var items = clientService.getList("http://localhost:8082/api/items?size=100", ItemModel.class);

            var keyboard = inlineKeyboardBuilder.buildListKeyboard(
                    items,
                    ItemModel::getName,
                    i -> "admin_menu:item:" + i.getId(),
                    1,
                    null
            );
            var text = "Выберите вещь для редактирования: ";
            senderService.sendAnswerWithKeyboard(chatId, text, keyboard);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void chooseMainImageMenu(Long chatId, ItemDraftCreateDto draft) {
        var imagesDto = itemDraftService.getItemImages(draft.getId());

        String itemInfo = "Выберите главное изображение";
        var images = imagesDto.stream()
                .map(i -> ImageModel.builder().image(i.getImage()).build())
                .toList();
        var keyboard = createMainImageKeyboard(draft.getId(), imagesDto);

        System.out.println("choosing main image");
        senderService.sendMediaGroup(chatId, images, keyboard, draft.getId(), itemInfo);
    }

    private InlineKeyboardMarkup createMainImageKeyboard(Long draftId, List<ImageDraft> images) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            row.add(createButton(String.valueOf(i + 1),
                    "admin_menu:main_image:" + draftId + ":" + images.get(i).getId()));

            if (row.size() == 5) {
                keyboard.add(new ArrayList<>(row));
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(new ArrayList<>(row));
        }

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;

    }

    private InlineKeyboardMarkup createEditImagesRemoveKeyboard(List<ImageDraft> images, Long draftId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            row.add(createButton(String.valueOf(i + 1),
                    "admin_menu:delete_image:" + draftId + ":" + images.get(i).getId()));

            if (row.size() == 5) {
                keyboard.add(new ArrayList<>(row));
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(new ArrayList<>(row));
        }

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup createEditImagesKeyboard(Long itemId) {
        var keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(createButton("➕ Добавить изображение", "admin_menu:edit_images_add:" + itemId));
        row.add(createButton("➖ Удалить изображение", "admin_menu:edit_images_delete:" + itemId));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("✅ Готово", "admin_menu:edit_images_ready:" + itemId));
        keyboard.add(new ArrayList<>(row));

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup createEditSizesKeyboard(Long itemId) {
        var keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(createButton("➕ Добавить размер", "admin_menu:edit_sizes_add:" + itemId));
        row.add(createButton("➖ Удалить размер", "admin_menu:edit_sizes_delete:" + itemId));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("✅ Готово", "admin_menu:edit_sizes_ready:" + itemId));
        keyboard.add(new ArrayList<>(row));

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup createEditItemKeyboard(Long itemId) {
        var keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(createButton("\uD83C\uDD70\uFE0F Название", "admin_menu:edit_name:" + itemId));
        row.add(createButton("\uD83D\uDCD4 Описание", "admin_menu:edit_description:" + itemId));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("\uD83D\uDCB8 Цена", "admin_menu:edit_price:" + itemId));
        row.add(createButton("\uD83D\uDCB8 Количество", "admin_menu:edit_quantity:" + itemId));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("\uD83E\uDDBF Бренд", "admin_menu:edit_brand:" + itemId));
        row.add(createButton("\uD83E\uDDE5 Категория", "admin_menu:edit_category:" + itemId));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("\uD83D\uDCAF Размеры", "admin_menu:edit_sizes:" + itemId));
        row.add(createButton("\uD83C\uDF05 Изображения", "admin_menu:edit_images:" + itemId));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("✅ Сохранить", "admin_menu:edit_save:" + itemId));
        keyboard.add(new ArrayList<>(row));

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup createAdminKeyboard() {
        var keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(createButton("➕ Добавить вещь", "admin_menu:add"));
        row.add(createButton("✏\uFE0F Изменить вещь", "admin_menu:edit"));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("\uD83C\uDCCF Добавить бренд", "admin_menu:add_brand"));
        row.add(createButton("\uD83E\uDE72 Добавить категорию", "admin_menu:add_category"));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("\uD83D\uDCDD Изменить по списку", "admin_menu:edit_list"));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("\uD83C\uDFE0 Главное меню", "main_menu:main_menu"));
        keyboard.add(new ArrayList<>(row));

        keyboardMarkup.setKeyboard(keyboard);


        return keyboardMarkup;
    }

    private String formatItemInfo(ItemDraftCreateDto item) {
        StringBuilder info = new StringBuilder();
        info.append("<b>").append(item.getName()).append("</b>\n\n");

        if (item.getDescription() != null) {
            info.append(item.getDescription()).append("\n\n");
        }

        if (item.getPrice() != null) {
            info.append("💰 Цена: ").append(item.getPrice()).append(" руб.\n");
        }

        var sizes = itemDraftService.getItemSizes(item.getId());

        if (sizes != null && !sizes.isEmpty()) {
            StringBuilder sizesBuilder = new StringBuilder();
            for (var size : sizes) {
                if (!sizesBuilder.isEmpty()) {
                    sizesBuilder.append(", ");
                }
                sizesBuilder.append(size);
            }
            info.append("\uD83D\uDD20 Размеры: ").append(sizesBuilder).append("\n");
        }

        if (item.getQuantity() != null) {
            info.append("\uD83D\uDD22 В наличии: ").append(item.getQuantity()).append(" шт.\n");
        }

        if (item.getBrand() != null) {
            var brand = getBrandName(item.getBrand());
            info.append("\uD83D\uDD4E Бренд: ").append(brand).append("\n");
        }

        if (item.getCategory() != null) {
            var brand = getCategoryName(item.getCategory());
            info.append("\uD83E\uDE70 Категория: ").append(brand).append("\n");
        }

        return info.toString();
    }

    private String getCategoryName(Long categoryId) {
        try {
            var category = clientService.getObject("http://localhost:8082/api/categories/" + categoryId, CategoryModel.class);
            return category.getName();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private String getBrandName(Long brandId) {
        try {
            var brand = clientService.getObject("http://localhost:8082/api/brands/" + brandId, BrandModel.class);
            return brand.getName();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


    private void editMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode("HTML");
        editMessage.setReplyMarkup(keyboard);
        producerService.produceEditMessage(editMessage);
    }

    private InlineKeyboardMarkup createEmptyKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText("\uD83C\uDFE0 Главное меню");
        backBtn.setCallbackData("main_menu");

        rows.add(List.of(backBtn));
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    private InlineKeyboardMarkup createMainMenuKeyboard(boolean isAdmin) {
        var keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(createButton("\uD83D\uDCC3 Посмотреть наличие", "main_menu:stock"));
        row.add(createButton("\uD83C\uDFEE Поиск по бренду", "main_menu:brands"));
        keyboard.add(new ArrayList<>(row));
        row.clear();

        row.add(createButton("\uD83D\uDC55 Поиск по категории", "main_menu:categories"));

        if (isAdmin) {
            row.add(createButton("\uD83D\uDD27 Админка", "admin_menu:admin_menu"));
        }

        keyboard.add(new ArrayList<>(row));
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

}
