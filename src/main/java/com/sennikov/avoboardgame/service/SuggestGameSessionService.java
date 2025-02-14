package com.sennikov.avoboardgame.service;

import com.sennikov.avoboardgame.bot.BoardGamesBot;
import com.sennikov.avoboardgame.dto.SuggestGameSession;
import com.sennikov.avoboardgame.model.BoardGame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SuggestGameSessionService {
    private final BoardGamesBot bot;

    public SuggestGameSession createSession(BoardGame game, LocalDateTime dateTime, String location, String chatId) {
        try {
            SuggestGameSession session = new SuggestGameSession();
            session.setGame(game);
            session.setSessionDateTime(dateTime.minusHours(5));
            session.setLocation(location);
            session.setChatId(chatId);

            SendPoll poll = createPoll(game, dateTime, location, chatId);
            Message message = bot.execute(poll);
            session.setPollMessageId(message.getMessageId().toString());

            return session;
        } catch (TelegramApiException e) {
            log.error("Error creating game session", e);
            throw new RuntimeException("Failed to create game session", e);
        }
    }

    private SendPoll createPoll(BoardGame game, LocalDateTime dateTime, String location, String chatId) {
        SendPoll poll = new SendPoll();
        poll.setChatId(chatId);
        poll.setQuestion(formatPollQuestion(game, dateTime, location));
        poll.setOptions(Arrays.asList("✅ Скорей же пойду", "❌ Нет, не могу я занят"));
        poll.setIsAnonymous(false);
        return poll;
    }

    private String formatPollQuestion(BoardGame game, LocalDateTime dateTime, String location) {
        return String.format("🎲 %s\n📅 %s\n📍 %s",
                game.getName(),
                dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                location);
    }
}