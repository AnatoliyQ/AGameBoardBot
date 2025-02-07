package com.sennikov.avoboardgame.bot;

import com.sennikov.avoboardgame.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("boardGamesBot")
@Slf4j
public class BoardGamesBot extends SpringWebhookBot {
    private final BotConfig config;

    public BoardGamesBot(BotConfig config, SetWebhook setWebhook) {
        super(setWebhook, config.getBotToken());
        this.config = config;
    }

    @Override
    public String getBotPath() {
        return config.getBotPath();
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            log.info("Received update: {}", update);

            if (update == null) {
                log.error("Received null update");
                return null;
            }

            Long userId = null;
            String source = "unknown";

            if (update.hasMessage()) {
                userId = update.getMessage().getFrom().getId();
                source = "message";
            } else if (update.hasCallbackQuery()) {
                userId = update.getCallbackQuery().getFrom().getId();
                source = "callback";
            }

            log.info("Extracted userId: {} from source: {}", userId, source);

            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                log.info("Processing message text: '{}' from chatId: {} and userId: {}", messageText, chatId, userId);

                if (messageText.startsWith("/start") || messageText.contains("startapp")) {
                    log.info("Creating main menu for start command/direct link. ChatId: {}, UserId: {}", chatId, userId);
                    return createMainMenu(chatId, userId);
                }
            }

            if (update.hasMessage() && update.getMessage().getViaBot() != null) {
                Message message = update.getMessage();
                if (message.getViaBot().getUserName().equals(getBotUsername())) {
                    log.info("Received message via attachment menu from userId: {}", userId);
                    return createMainMenu(message.getChatId(), userId);
                }
            }

            if (update.hasCallbackQuery()) {
                log.info("Processing callback query from userId: {}", userId);
                return handleCallbackQuery(update.getCallbackQuery());
            }

            if (update.hasMessage()) {
                log.info("Processing regular message from userId: {}", userId);
                return handleMessage(update.getMessage(), userId);
            }

            log.warn("Update wasn't handled. Type: {}, UserId: {}", update.getClass().getSimpleName(), userId);
            return null;

        } catch (Exception e) {
            log.error("Error processing update: {} for userId: {}", e.getMessage(),
                    update != null && update.hasMessage() ? update.getMessage().getFrom().getId() : "unknown", e);
            return null;
        }
    }

    private BotApiMethod<?> handleAttachmentMenuMessage(Message message) {
        SendMessage response = new SendMessage();
        response.setChatId(message.getChatId().toString());
        response.setText("Приложение запущено через меню вложений");
        return createWebAppButton(message.getChatId(), message.getFrom().getId());
    }

    private BotApiMethod<?> handleSettingsButton(CallbackQuery callbackQuery) {
        SendMessage response = new SendMessage();
        response.setChatId(callbackQuery.getMessage().getChatId().toString());
        response.setText("Настройки приложения");
        return response;
    }

    private SendMessage createWebAppButton(Long chatId, Long userId) {
        log.info("Creating WebApp button for chat: {} and user: {}", chatId, userId);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Нажмите кнопку, чтобы открыть приложение");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton webAppButton = new InlineKeyboardButton();
        webAppButton.setText("Открыть приложение");

        String webAppUrl = config.getWebappUrl() + "?tgUserId=" + userId;
        log.info("Setting WebApp URL: {} for userId: {}", webAppUrl, userId);

        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(webAppUrl);
        webAppButton.setWebApp(webAppInfo);

        keyboard.add(Collections.singletonList(webAppButton));
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        return message;
    }

    private BotApiMethod<?> handleMessage(Message message, Long userId) {
        if (message.getChat().getType().equals("private")) {
            Long chatId = message.getChatId();
            log.info("Handling private message. ChatId: {}, UserId: {}", chatId, userId);

            if (message.hasText()) {
                String messageText = message.getText();
                log.info("Message text: '{}' from userId: {}", messageText, userId);

                if (messageText.startsWith("/start") || messageText.contains("startapp")) {
                    log.info("Creating main menu from handleMessage. ChatId: {}, UserId: {}", chatId, userId);
                    return createMainMenu(chatId, userId);
                }

                switch (messageText) {
                    case "/help":
                        return createHelpMessage(chatId);
                    default:
                        return createDefaultResponse(chatId);
                }
            }
        }
        return null;
    }

    private BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        try {
            log.info("Handling callback query: {}", callbackQuery);
            return null;
        } catch (Exception e) {
            log.error("Error handling callback query: {}", e.getMessage(), e);
            return null;
        }
    }

    private SendMessage createHelpMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Доступные команды:\n" +
                "/start - Открыть главное меню\n" +
                "/help - Показать это сообщение");
        return message;
    }

    private SendMessage createDefaultResponse(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Извините, я не понимаю эту команду. Используйте /help для списка команд.");
        return message;
    }

    private SendMessage createMainMenu(long chatId, Long userId) {
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText("Добро пожаловать в Board Games Bot!");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton webAppButton = new InlineKeyboardButton();
        webAppButton.setText("Открыть приложение");

        String webAppUrl = config.getWebappUrl() + "?tgWebAppStart=true";
        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(webAppUrl);
        webAppButton.setWebApp(webAppInfo);

        keyboard.add(Collections.singletonList(webAppButton));
        markup.setKeyboard(keyboard);
        response.setReplyMarkup(markup);

        return response;
    }
}
