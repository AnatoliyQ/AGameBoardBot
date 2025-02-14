package com.sennikov.avoboardgame.controller;

import com.sennikov.avoboardgame.dto.BoardGameDto;
import com.sennikov.avoboardgame.dto.SuggestGameSession;
import com.sennikov.avoboardgame.dto.SuggestSessionRequest;
import com.sennikov.avoboardgame.exception.EntityNotFoundException;
import com.sennikov.avoboardgame.model.BoardGame;
import com.sennikov.avoboardgame.service.BoardGameService;
import com.sennikov.avoboardgame.service.SuggestGameSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/sessions")
@Slf4j
@Validated
@RequiredArgsConstructor
public class SuggestGameSessionController {
    private final SuggestGameSessionService sessionService;
    private final BoardGameService gameService;

    @PostMapping("")
    public ResponseEntity<SuggestGameSession> createSession(@Valid @RequestBody SuggestSessionRequest request) {
        try {
            BoardGameDto gameDto = gameService.getGameById(request.getGameId())
                    .orElseThrow(() -> new EntityNotFoundException("Игра не найдена с ID: " + request.getGameId()));

            BoardGame game = convertToEntity(gameDto);
            
            // Парсим локальное время Ташкента
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(request.getDateTime(), formatter);

            SuggestGameSession session = sessionService.createSession(
                    game,
                    dateTime,
                    request.getLocation(),
                    "-4692104992"
            );

            return ResponseEntity.ok(session);
        } catch (EntityNotFoundException e) {
            log.error("Game not found", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error creating session", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private BoardGame convertToEntity(BoardGameDto dto) {
        BoardGame game = new BoardGame();
        game.setId(dto.getId());
        game.setName(dto.getName());
        game.setType(dto.getType());
        game.setPlayers(dto.getPlayers());
        game.setOwner(dto.getOwner());
        return game;
    }
}
