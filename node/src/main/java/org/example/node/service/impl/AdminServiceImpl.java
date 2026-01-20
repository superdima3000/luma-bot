package org.example.node.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonjpa.entity.AppUser;
import org.example.commonjpa.entity.dto.BrandDraftCreateDto;
import org.example.commonjpa.entity.dto.CategoryDraftCreateDto;
import org.example.commonjpa.entity.dto.ItemDraftCreateDto;
import org.example.commonjpa.entity.dto.SizeDraftCreateDto;
import org.example.commonjpa.repository.AppUserRepository;
import org.example.commonjpa.repository.ImageDraftRepository;
import org.example.commonjpa.service.ItemDraftService;
import org.example.commonjpa.service.UserService;
import org.example.http.model.BrandModel;
import org.example.http.model.CategoryModel;
import org.example.http.model.ItemModel;
import org.example.http.model.SizeModel;
import org.example.http.model.dto.ImageFileDto;
import org.example.http.service.ClientService;
import org.example.node.keyboard.InlineKeyboardBuilder;
import org.example.node.service.AdminService;
import org.example.node.service.FileService;
import org.example.node.service.MenuService;
import org.example.node.service.SenderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.example.commonjpa.entity.enums.State.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final SenderService senderService;
    private final AppUserRepository appUserRepository;
    private final ItemDraftService itemDraftService;
    private final InlineKeyboardBuilder inlineKeyboardBuilder;
    private final ClientService clientService;
    private final FileService fileService;
    private final MenuService menuService;
    private final UserService userService;
    private final ImageDraftRepository imageDraftRepository;


    @Override
    public void addItem(Long chatId, Integer messageId, Long userId) {
        AppUser persistedAppUser = userService.changeState(userId, WAITING_FOR_ITEM_NAME);
        var sessionId = ThreadLocalRandom.current().nextLong(1_000_000, 9_999_999_999L);
        while (appUserRepository.existsBySessionId(sessionId)) {
            sessionId = ThreadLocalRandom.current().nextLong(1_000_000, 9_999_999_999L);
        }

        persistedAppUser.setSessionId(sessionId);
        appUserRepository.save(persistedAppUser);
        senderService.sendAnswer(chatId, "Введите название вещи: ");
    }

    @Override
    public void clearDrafts(Long userId) {
        var persistedAppUser = appUserRepository.findByUserId(userId);
        var sessionId = persistedAppUser.getSessionId();
        itemDraftService.deleteItemDraft(sessionId);
        persistedAppUser.setSessionId(null);
        persistedAppUser.setState(ACTIVE);
        appUserRepository.save(persistedAppUser);
    }

    @Override
    public void addItemName(Long chatId, Integer messageId, String name, Long userId) {
        if (name == null){
            senderService.sendAnswer(chatId, "Введите название вещи: ");
            return;
        }

        AppUser persistedAppUser = userService.changeState(userId, WAITING_FOR_ITEM_DESCRIPTION);
        var sessionId = persistedAppUser.getSessionId();
        var draft = ItemDraftCreateDto.builder()
                .name(name)
                .sessionId(sessionId)
                .build();

        itemDraftService.createItemDraft(draft);
        appUserRepository.save(persistedAppUser);

        senderService.sendAnswer(chatId, "Введите описание вещи: ");
    }

    @Override
    public void addItemDescription(Long chatId, Integer messageId, String description, Long userId) {
        var persistedAppUser = userService.changeState(userId, WAITING_FOR_ITEM_PRICE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);
        draft.setDescription(description);

        itemDraftService.updateItemDraft(draft.getId(), draft);
        persistedAppUser.setState(WAITING_FOR_ITEM_PRICE);
        appUserRepository.save(persistedAppUser);

        senderService.sendAnswer(chatId, "Введите цену вещи: ");
    }

    @Override
    public void addItemPrice(Long chatId, Integer messageId, String price, Long userId) {
        try {
            Double number = Double.parseDouble(price);
            var persistedAppUser = userService.changeState(userId, WAITING_FOR_ITEM_SIZES);
            var sessionId = persistedAppUser.getSessionId();
            var draft = itemDraftService.getItemDraft(sessionId);

            draft.setPrice(number);

            itemDraftService.updateItemDraft(draft.getId(), draft);
            appUserRepository.save(persistedAppUser);

            senderService.sendAnswer(chatId, "Введите размер вещи: ");

        } catch (NumberFormatException e) {
            senderService.sendAnswer(chatId, "Введите цену вещи: ");
        }
    }

    @Override
    public void addItemSizes(Long chatId, Integer messageId, String size, Long userId) {
        var persistedAppUser = appUserRepository.findByUserId(userId);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);


        itemDraftService.addSizeToItem(draft.getId(), size);

        var sizes = itemDraftService.getItemSizes(draft.getId());
        var keyboard = createSizesReadyKeyboard();
        senderService.sendAnswerWithKeyboard(chatId,
                "Вы добавили размеры: " + String.join(", ", sizes) +"\nВведите размер: ",
                keyboard);
    }

    @Override
    public void addItemBrand(Long chatId, Integer messageId, Long userId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);

        var keyboard = createBrandsKeyboard(false);

        senderService.sendAnswerWithKeyboard(chatId, "Выберите бренд: ", keyboard);
    }

    private InlineKeyboardMarkup createBrandsKeyboard(boolean edit) {
        try {
            var brands = clientService.getList("http://localhost:8082/api/brands", BrandModel.class);
            String callbackData = edit ? "admin_menu:edit_brands:" : "admin_menu:brands:";
            return inlineKeyboardBuilder.buildListKeyboard(
                    brands,
                    BrandModel::getName,
                    b -> callbackData + b.getId(),
                    2,
                    null
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addItemBrandProceed(Long chatId, Integer messageId, Long userId, Long brandId) {
        var persistedAppUser = appUserRepository.findByUserId(userId);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        draft.setBrand(brandId);
        itemDraftService.updateItemDraft(draft.getId(), draft);

        var keyboard = createCategoriesKeyboard(false);

        senderService.sendAnswerWithKeyboard(chatId, "Выберите категорию: ", keyboard);
    }

    @Override
    public void addItemCategoryProceed(Long chatId, Integer messageId, Long userId, Long categoryId) {
        var persistedAppUser = userService.changeState(userId, WAITING_FOR_ITEM_QUANTITY);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        draft.setCategory(categoryId);
        itemDraftService.updateItemDraft(draft.getId(), draft);

        appUserRepository.save(persistedAppUser);

        senderService.sendAnswer(chatId, "Введите количество вещей в наличии: ");
    }

    @Override
    public void addItemQuantity(Long chatId, Integer messageId, String quantity, Long userId) {
        try {
            var quantityInt = Integer.parseInt(quantity);
            var persistedAppUser = userService.changeState(userId, WAITING_FOR_ITEM_IMAGES);
            var sessionId = persistedAppUser.getSessionId();
            var draft = itemDraftService.getItemDraft(sessionId);

            draft.setQuantity(quantityInt);
            itemDraftService.updateItemDraft(draft.getId(), draft);

            appUserRepository.save(persistedAppUser);

            senderService.sendAnswer(chatId, "Отправьте изображения вещи: ");
        } catch (NumberFormatException e) {
            senderService.sendAnswer(chatId, "Введите количество вещей в наличии: ");
        }
    }

    @Override
    public void addItemImages(Long chatId, Integer messageId, Long userId, List<PhotoSize> photos, String mediaGroupId) {
        PhotoSize largestPhoto = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(photos.getLast());
        if (mediaGroupId != null && !mediaGroupId.isEmpty()) {
            fileService.handleMediaGroupPhoto(mediaGroupId, largestPhoto, fileNames -> {
                System.out.println("Получены файлы из медиагруппы: " + fileNames);

                var persistedAppUser = appUserRepository.findByUserId(userId);
                var sessionId = persistedAppUser.getSessionId();

                for (int i = 0; i < fileNames.size(); i++) {
                    var fileName = fileNames.get(i);
                    itemDraftService.addImageToItem(sessionId, fileName, i == 0);
                }

                senderService.sendAnswer(chatId, "Все фото загружены! Получено " + fileNames.size() + " файлов");

                finalizeAddItem(chatId, userId, messageId);
            });
        } else {
            fileService.downloadSinglePhoto(largestPhoto, false, null, null);

            var persistedAppUser = appUserRepository.findByUserId(userId);
            var sessionId = persistedAppUser.getSessionId();

            String fileName = "images/img_" + largestPhoto.getFileId() + ".jpg";
            itemDraftService.addImageToItem(sessionId, fileName, true);

            senderService.sendAnswer(chatId, "Фото загружено!");
            finalizeAddItem(chatId, userId, messageId);
        }
    }

    @Override
    public void addCategory(Long chatId, Integer messageId, Long userId) {
        AppUser persistedAppUser = userService.changeState(userId, WAITING_FOR_CATEGORY_NAME);
        senderService.sendAnswer(chatId, "Введите название категории: ");
    }

    @Override
    public void addBrand(Long chatId, Integer messageId, Long userId) {
        AppUser persistedAppUser = userService.changeState(userId, WAITING_FOR_BRAND_NAME);
        senderService.sendAnswer(chatId, "Введите название бренда: ");
    }

    @Override
    public void addBrandProceed(Long chatId, Integer messageId, String text, Long userId) {
        if (text == null) {
            senderService.sendAnswer(chatId, "Введите название бренда: ");
            return;
        }

        AppUser persistedAppUser = userService.changeState(userId, ACTIVE);
        var brand = BrandDraftCreateDto.builder()
                .name(text)
                .build();

        clientService.postObject("http://localhost:8082/api/brands", brand, BrandModel.class);
        appUserRepository.save(persistedAppUser);
        senderService.sendAnswer(chatId, "Бренд добавлен!");
        menuService.showAdminMenu(chatId, messageId);
    }

    @Override
    public void editItem(Long chatId, Integer messageId, Long userId) {
        AppUser persistedAppUser = userService.changeState(userId, WAITING_FOR_ITEM_NAME_SEARCH);
        senderService.sendAnswer(chatId, "Введите название вещи: ");
    }

    @Override
    public void showItemsForName(Long chatId, Integer messageId, String text, Long userId){
        if (text == null) {
            senderService.sendAnswer(chatId, "Введите название вещи: ");
            return;
        }
        menuService.showItemsForName(chatId, messageId, text);

        AppUser persistedAppUser = userService.changeState(userId, ACTIVE);

    }

    @Override
    public void editItemMenu(Long chatId, Integer messageId, Long userId, Long itemId){
        AppUser persistedAppUser = appUserRepository.findByUserId(userId);
        var sessionId = ThreadLocalRandom.current().nextLong(1_000_000, 9_999_999_999L);
        while (appUserRepository.existsBySessionId(sessionId)) {
            sessionId = ThreadLocalRandom.current().nextLong(1_000_000, 9_999_999_999L);
        }

        persistedAppUser.setSessionId(sessionId);
        appUserRepository.save(persistedAppUser);
        try {
            var item = clientService.getObject("http://localhost:8082/api/items/" + itemId, ItemModel.class);
            var draft = itemDraftService.mapFromModel(item);

            draft.setSessionId(sessionId);
            draft.setItemId(itemId);
            var newId = itemDraftService.createItemDraft(draft).getId();
            draft.setId(newId);


            menuService.showItemEditMenu(chatId, draft);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addItemNameEdit(Long chatId, Integer messageId, String text, Long userId) {
        if (text == null){
            senderService.sendAnswer(chatId, "Введите название вещи: ");
            return;
        }

        AppUser persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        draft.setName(text);
        itemDraftService.updateItemDraft(draft.getId(), draft);

        menuService.showItemEditMenu(chatId, draft);
    }

    @Override
    public void addItemDescriptionEdit(Long chatId, Integer messageId, String text, Long userId) {
        if (text == null){
            senderService.sendAnswer(chatId, "Введите описание: ");
            return;
        }

        AppUser persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);
        draft.setDescription(text);
        itemDraftService.updateItemDraft(draft.getId(), draft);
        menuService.showItemEditMenu(chatId, draft);
    }

    @Override
    public void addItemPriceEdit(Long chatId, Integer messageId, String text, Long userId) {
        try {
            Double number = Double.parseDouble(text);
            var persistedAppUser = userService.changeState(userId, ACTIVE);
            var sessionId = persistedAppUser.getSessionId();
            var draft = itemDraftService.getItemDraft(sessionId);

            draft.setPrice(number);

            itemDraftService.updateItemDraft(draft.getId(), draft);
            menuService.showItemEditMenu(chatId, draft);

        } catch (NumberFormatException e) {
            senderService.sendAnswer(chatId, "Введите цену вещи: ");
        }
    }

    @Override
    public void addItemQuantityEdit(Long chatId, Integer messageId, String text, Long userId) {
        try {
            var quantityInt = Integer.parseInt(text);
            var persistedAppUser = userService.changeState(userId, ACTIVE);
            var sessionId = persistedAppUser.getSessionId();
            var draft = itemDraftService.getItemDraft(sessionId);

            draft.setQuantity(quantityInt);

            itemDraftService.updateItemDraft(draft.getId(), draft);
            menuService.showItemEditMenu(chatId, draft);
        } catch (NumberFormatException e) {
            senderService.sendAnswer(chatId, "Введите количество вещей в наличии: ");
        }
    }

    @Override
    public void editItemCategory(Long chatId, Integer messageId, Long userId) {
        var keyboard = createCategoriesKeyboard(true);
        senderService.sendAnswerWithKeyboard(chatId, "Выберите категорию: ", keyboard);
    }

    @Override
    public void editItemCategoryProceed(Long chatId, Integer messageId, Long userId, Long categoryId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        draft.setCategory(categoryId);
        itemDraftService.updateItemDraft(draft.getId(), draft);
        menuService.showItemEditMenu(chatId, draft);
    }

    @Override
    public void editItemBrand(Long chatId, Integer messageId, Long userId) {
        var keyboard = createBrandsKeyboard(true);
        senderService.sendAnswerWithKeyboard(chatId, "Выберите бренд: ", keyboard);
    }

    @Override
    public void editItemBrandProceed(Long chatId, Integer messageId, Long userId, Long brandId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        draft.setBrand(brandId);
        itemDraftService.updateItemDraft(draft.getId(), draft);
        menuService.showItemEditMenu(chatId, draft);
    }

    @Override
    public void editItemSizes(Long chatId, Integer messageId, Long userId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        menuService.showEditSizeMenu(chatId, draft);
    }

    @Override
    public void editItemSizesAdd(Long chatId, Integer messageId, Long userId, String size) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        itemDraftService.addSizeToItem(draft.getId(), size);
        menuService.showEditSizeMenu(chatId, draft);
    }

    @Override
    public void editItemSizesRemoveMenu(Long chatId, Integer messageId, Long userId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        menuService.showEditSizeRemoveMenu(chatId, draft);
    }

    @Override
    public void editItemSizesRemove(Long chatId, Integer messageId, Long userId, String size) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        itemDraftService.removeSizeFromItem(draft.getId(), size);
        menuService.showEditSizeMenu(chatId, draft);
    }

    @Override
    public void finalizeEditSizes(Long chatId, Integer messageId, Long userId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        menuService.showItemEditMenu(chatId, draft);
    }

    @Override
    public void editItemImages(Long chatId, Integer messageId, Long userId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        menuService.showItemEditImagesMenu(chatId, draft);
    }

    @Override
    public void editItemImagesAdd(Long chatId, Integer messageId, Long userId, List<PhotoSize> photos) {
        PhotoSize largestPhoto = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(photos.getLast());

        fileService.downloadSinglePhoto(largestPhoto, true, chatId, userId);
    }

    @Override
    public void editItemImagesAddProceed(Long chatId, Long userId, ImageFileDto dto){
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();

        itemDraftService.addImageToItem(sessionId, dto.getFilePath(), false);

        var draft = itemDraftService.getItemDraft(sessionId);
        menuService.showItemEditImagesMenu(chatId, draft);
    }

    @Override
    public void choseMainImage(Long chatId, Integer messageId, Long userId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();

        var draft = itemDraftService.getItemDraft(sessionId);
        menuService.chooseMainImageMenu(chatId, draft);
    }

    @Override
    public void editItemImagesRemoveMenu(Long chatId, Integer messageId, Long userId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        menuService.showEditImagesRemoveMenu(chatId, draft);
    }

    @Override
    public void editItemImagesRemove(Long chatId, Integer messageId, Long userId, Long imageId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        itemDraftService.removeImageFromItem(draft.getId(), imageId);
        menuService.showItemEditImagesMenu(chatId, draft);
    }

    @Override
    public void finalizeEditImages(Long chatId, Integer messageId, Long userId, Long imageId) {
        var persistedAppUser = userService.changeState(userId, ACTIVE);
        var sessionId = persistedAppUser.getSessionId();
        var draft = itemDraftService.getItemDraft(sessionId);

        System.out.println(imageId);

        var images = itemDraftService.getItemImages(draft.getId());
        for (var image : images) {
            if(image.getId().equals(imageId)){
                image.setIsMain(true);
            } else {
                image.setIsMain(false);
            }
            imageDraftRepository.save(image);
        }


        menuService.showItemEditMenu(chatId, draft);
    }

    @Override
    public void finalizeEditItem(Long chatId, Integer messageId, Long userId, Long itemId) {
        var persistedAppUser = appUserRepository.findByUserId(userId);
        var sessionId = persistedAppUser.getSessionId();

        var draft = itemDraftService.getItemDraft(sessionId);
        var originalId = draft.getItemId();
        log.debug("originalId: " + originalId);
        var item = clientService.putObject("http://localhost:8082/api/items/" + originalId, draft, ItemModel.class);

        System.out.println(item);
        clientService.sendDeleteRequest("http://localhost:8082/api/sizes/item/" + originalId);
        addItemSizes(chatId, messageId, persistedAppUser, sessionId, draft, item);
    }

    private void addItemSizes(Long chatId, Integer messageId, AppUser persistedAppUser, Long sessionId, ItemDraftCreateDto draft, ItemModel item) {
        var sizes = itemDraftService.getItemSizes(draft.getId());
        for (var size : sizes) {
            var sizeEntity = SizeDraftCreateDto.builder()
                    .name(size)
                    .itemId(item.getId())
                    .build();
            clientService.postObject("http://localhost:8082/api/sizes", sizeEntity, SizeModel.class);
        }


        itemDraftService.deleteItemDraft(sessionId);
        persistedAppUser.setState(ACTIVE);
        appUserRepository.save(persistedAppUser);
        senderService.sendAnswer(chatId, "Вещь добавлена!");

        menuService.showAdminMenu(chatId, messageId);
    }

    @Override
    public void addCategoryProceed(Long chatId, Integer messageId, String text, Long userId) {
        if (text == null) {
            senderService.sendAnswer(chatId, "Введите название категории: ");
            return;
        }
        AppUser persistedAppUser = userService.changeState(userId, ACTIVE);
        var category = CategoryDraftCreateDto.builder()
                .name(text)
                .build();

        clientService.postObject("http://localhost:8082/api/categories", category, CategoryModel.class);
        appUserRepository.save(persistedAppUser);
        senderService.sendAnswer(chatId, "Категория добавлена!");
        menuService.showAdminMenu(chatId, messageId);
    }

    private void finalizeAddItem(Long chatId, Long userId, Integer messageId) {
        var persistedAppUser = appUserRepository.findByUserId(userId);
        var sessionId = persistedAppUser.getSessionId();

        var draft = itemDraftService.getItemDraft(sessionId);
        var item = clientService.postObject("http://localhost:8082/api/items", draft, ItemModel.class);

        addItemSizes(chatId, messageId, persistedAppUser, sessionId, draft, item);
    }

    private InlineKeyboardMarkup createCategoriesKeyboard(boolean edit) {
        try {
            var categories = clientService.getList("http://localhost:8082/api/categories", CategoryModel.class);
            String callbackData;
            if (edit) {
                callbackData = "admin_menu:edit_categories:";
            } else {
                callbackData = "admin_menu:categories:";
            }
            return inlineKeyboardBuilder.buildListKeyboard(
                    categories,
                    CategoryModel::getName,
                    c -> callbackData + c.getId(),
                    2,
                    null
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardMarkup createSizesReadyKeyboard(){
        var keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("✅ Готово");
        inlineKeyboardButton.setCallbackData("admin_menu:sizes_ready");
        row.add(inlineKeyboardButton);
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
