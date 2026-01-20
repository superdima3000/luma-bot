package org.example.node.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonjpa.entity.AppUser;
import org.example.commonjpa.entity.MediaGroupModel;
import org.example.commonjpa.entity.enums.Role;
import org.example.commonjpa.entity.enums.State;
import org.example.commonjpa.repository.AppUserRepository;
import org.example.commonjpa.repository.MediaGroupRepository;
import org.example.http.model.BrandModel;
import org.example.http.model.CategoryModel;
import org.example.http.model.ItemModel;
import org.example.http.model.dto.ImageFileDto;
import org.example.http.model.dto.MediaGroupSentDto;
import org.example.http.service.ClientService;
import org.example.node.callback.CallbackQueryDispatcher;
import org.example.node.keyboard.InlineKeyboardBuilder;
import org.example.node.keyboard.StaticKeyboard;
import org.example.node.service.*;
import org.example.node.service.enums.ServiceCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static org.example.commonjpa.entity.enums.State.*;
import static org.example.commonjpa.entity.enums.State.PASSWORD;
import static org.example.node.keyboard.KeyboardConstants.*;
import static org.example.node.service.enums.ServiceCommands.*;

@Service
@Slf4j
public class MainServiceImpl implements MainService {

    private final MenuService menuService;
    private final AppUserRepository appUserRepository;
    private final MediaGroupRepository mediaGroupRepository;
    private final SenderService senderService;
    private final CallbackQueryDispatcher callbackDispatcher;
    private final String password;
    private final AdminService adminService;
    private final FileService fileService;

    public MainServiceImpl(
            MenuService menuService,
            AppUserRepository appUserRepository,
            MediaGroupRepository mediaGroupRepository,
            SenderService senderService,
            CallbackQueryDispatcher callbackDispatcher,
            @Value("${my.password}") String password,
            AdminService adminService, FileService fileService) {
        this.password = password;
        this.menuService = menuService;
        this.appUserRepository = appUserRepository;
        this.mediaGroupRepository = mediaGroupRepository;
        this.senderService = senderService;
        this.callbackDispatcher = callbackDispatcher;
        this.adminService = adminService;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update) {
        log.debug("processTextMessage");
        var message = update.getMessage();
        var chatId = message.getChatId();
        var messageId = message.getMessageId();
        var user = message.getFrom();
        var appUser = findOrSaveAppUser(user);
        var state = appUser.getState();
        var text = message.getText();
        var output = "";

        if (START.equals(text)) {
            adminService.clearDrafts(appUser.getUserId());
            mainMenu(message, appUser);
            return;
        }

        if (ACTIVE.equals(state)) {
            output = processServiceCommand(appUser, message);
        } else if (PASSWORD.equals(state)) {
            output = processPassword(appUser, message);
        } else if (WAITING_FOR_ITEM_NAME.equals(state)) {
            adminService.addItemName(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_DESCRIPTION.equals(state)) {
            adminService.addItemDescription(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_PRICE.equals(state)) {
            adminService.addItemPrice(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_SIZES.equals(state)) {
            adminService.addItemSizes(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_QUANTITY.equals(state)) {
            adminService.addItemQuantity(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_CATEGORY_NAME.equals(state)) {
            adminService.addCategoryProceed(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_BRAND_NAME.equals(state)) {
            adminService.addBrandProceed(chatId, messageId, text, user.getId());
        } else if(WAITING_FOR_ITEM_NAME_SEARCH.equals(state)) {
            adminService.showItemsForName(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_NAME_EDIT.equals(state)){
            adminService.addItemNameEdit(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_DESCRIPTION_EDIT.equals(state)) {
            adminService.addItemDescriptionEdit(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_PRICE_EDIT.equals(state)) {
            adminService.addItemPriceEdit(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_QUANTITY_EDIT.equals(state)) {
            adminService.addItemQuantityEdit(chatId, messageId, text, user.getId());
        } else if (WAITING_FOR_ITEM_SIZES_EDIT.equals(state)) {
            adminService.editItemSizesAdd(chatId, messageId, user.getId(), text);
        }
        else {
            log.error("Unknown state: {}", state);
            output = "Неизвестная ошибка!";
        }

        senderService.sendAnswer(chatId, output);
    }


    private String processPassword(AppUser appUser, Message message) {
        var text = message.getText();
        if (password.equals(text)) {
            proceedAuthenticatedUser(appUser, message);
            return "";
        } else {
            log.debug("wrong password: {}", text);
            proceedUnauthenticatedUser(appUser, message);
            return "Неверный пароль!";
        }
    }

    private void proceedAuthenticatedUser(AppUser appUser, Message message) {
        var persistedAppUser = appUserRepository.findByUserId(appUser.getUserId());
        persistedAppUser.setState(ACTIVE);
        persistedAppUser.setRole(Role.ADMIN);
        appUserRepository.save(persistedAppUser);
        menuService.showMainMenu(message.getChatId(), message.getMessageId(), true);
    }

    private void proceedUnauthenticatedUser(AppUser appUser, Message message) {
        var persistedUser = appUserRepository.findByUserId(appUser.getUserId());
        persistedUser.setState(ACTIVE);
        appUserRepository.save(persistedUser);
    }

    @Override
    public void processMediaGroupSent(MediaGroupSentDto dto){
        senderService.sendAnswerWithKeyboard(dto.getChatId(), dto.getItemInfo(), dto.getKeyboard());
        saveMediaGroup(dto);
    }

    @Override
    public void processImageDownloaded(ImageFileDto dto) {
        log.debug("received dto: {}", dto);
        if (dto.isEdit()){
            adminService.editItemImagesAddProceed(dto.getChatId(), dto.getUserId(), dto);
        }
    }

    private void saveMediaGroup(MediaGroupSentDto dto) {
        MediaGroupModel mediaGroup = MediaGroupModel.builder()
                .chatId(dto.getChatId())
                .itemId(dto.getItemId())
                .messageId(dto.getMessageId())
                .amount(dto.getAmount())
                .build();

        mediaGroupRepository.save(mediaGroup);
    }

    private String processServiceCommand(AppUser appUser, Message message) {
        var text = message.getText();

        if (HELP.equals(text)) {
            return help();
        }

        if (ServiceCommands.PASSWORD.equals(text)) {
            passwordAuthentification(message, appUser);
            return "";
        } else {
            return "Введите /start";
        }
    }

    private void mainMenu(Message message, AppUser appUser) {
        menuService.showMainMenu(message.getChatId(), message.getMessageId(), Role.ADMIN.equals(appUser.getRole()));
    }

    private void passwordAuthentification(Message message, AppUser appUser) {
        var persistedAppUser = appUserRepository.findByUserId(appUser.getUserId());
        persistedAppUser.setState(PASSWORD);
        appUserRepository.save(persistedAppUser);
        senderService.sendAnswer(message.getChatId(), "Введите пароль");
    }
    
    private String help() {
        return "Cписок команд:" +
               "/cancel - вернуться в меню";
    }


    private AppUser findOrSaveAppUser(User telegramUser) {
        AppUser persistentAppUser = appUserRepository.findByUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            //TODO пососать
            AppUser transientAppUser = AppUser.builder()
                    .userId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .username(telegramUser.getUserName())
                    .role(Role.USER)
                    .state(ACTIVE)
                    .build();

            return appUserRepository.save(transientAppUser);
        }
        return persistentAppUser;
    }

    @Override
    public void processCallbackQuery(Update update){
        callbackDispatcher.dispatch(update.getCallbackQuery());
    }

    @Override
    public void processPhotoMessage(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();
        var userId = user.getId();
        var appUser = findOrSaveAppUser(user);
        var state = appUser.getState();
        var photo = message.getPhoto();
        var chatId = message.getChatId();
        var messageId = message.getMessageId();
        var mediaGroupId = message.getMediaGroupId();

        if (WAITING_FOR_ITEM_IMAGES.equals(state)) {
            adminService.addItemImages(chatId, messageId, userId, photo, mediaGroupId);
        } else if (WAITING_FOR_ITEM_IMAGES_EDIT.equals(state)) {
            adminService.editItemImagesAdd(chatId, messageId, userId, photo);
        }

    }

}
