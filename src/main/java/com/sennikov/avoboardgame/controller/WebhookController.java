package com.sennikov.avoboardgame.controller;

import com.sennikov.avoboardgame.bot.BoardGamesBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@Slf4j
public class WebhookController {
    private final BoardGamesBot bot;

    public WebhookController(@Qualifier("boardGamesBot") BoardGamesBot bot) {
        this.bot = bot;
    }

    @PostMapping("/callback/webhook")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        log.info("Received webhook update: {}", update);
        try {
            BotApiMethod<?> response = bot.onWebhookUpdateReceived(update);
            log.info("Sending response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error processing webhook update {} {}", e, update);
            return null;
        }
    }

    @GetMapping("/callback/webhook")
    public String testWebhook() {
        return "Webhook endpoint is working";
    }
}