package com.sennikov.avoboardgame.config;

import com.sennikov.avoboardgame.bot.BoardGamesBot;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Data
@Slf4j
public class BotConfig {
    private String webhookPath;
    private String botUsername;
    private String botToken;
    private String botPath;
    private String webappUrl;

    @PostConstruct
    public void logConfig() {
        log.info("Bot configuration loaded:");
        log.info("Webhook Path: {}", webhookPath);
        log.info("Bot Username: {}", botUsername);
        log.info("Bot Path: {}", botPath);
        log.info("WebApp URL: {}", webappUrl);
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder()
                .url(getWebhookPath() + getBotPath())
                .build();
    }

    @Bean
    @Primary
    public BoardGamesBot boardGamesBot(SetWebhook setWebhook) {
        return new BoardGamesBot(this, setWebhook);
    }
}