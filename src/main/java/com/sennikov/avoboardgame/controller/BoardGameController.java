package com.sennikov.avoboardgame.controller;

import com.sennikov.avoboardgame.dto.BoardGameDto;
import com.sennikov.avoboardgame.dto.BoardGameRequest;
import com.sennikov.avoboardgame.service.BoardGameService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@Slf4j
@Validated
@RequiredArgsConstructor
public class BoardGameController {
    private static final List<Long> ADMIN_IDS = Arrays.asList(332120503L, 1204992915L);

    private final BoardGameService gameService;

    @GetMapping
    public ResponseEntity<List<BoardGameDto>> getAllGames() {
        log.info("Getting all games");
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGameDto> getGameById(@PathVariable Long id) {
        log.info("Getting game by id: {}", id);
        return gameService.getGameById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BoardGameDto> addGame(
            @Valid @RequestBody BoardGameRequest request,
            @RequestHeader(value = "X-Telegram-User-Id", required = false) Long userId) {
        log.info("Adding new game: {} by user: {}", request, userId);

        if (!isAdmin(userId)) {
            log.warn("Unauthorized attempt to add game by user: {}", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            BoardGameDto savedGame = gameService.saveGame(request);
            log.info("Successfully added game: {}", savedGame);
            return ResponseEntity.ok(savedGame);
        } catch (Exception e) {
            log.error("Error adding game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardGameDto> updateGame(
            @PathVariable Long id,
            @Valid @RequestBody BoardGameRequest request,
            @RequestHeader(value = "X-Telegram-User-Id", required = false) Long userId) {
        log.info("Updating game with id: {} by user: {}", id, userId);

        if (!isAdmin(userId)) {
            log.warn("Unauthorized attempt to update game by user: {}", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            BoardGameDto updatedGame = gameService.updateGame(id, request);
            log.info("Successfully updated game: {}", updatedGame);
            return ResponseEntity.ok(updatedGame);
        } catch (EntityNotFoundException e) {
            log.warn("Game not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable Long id,
            @RequestHeader(value = "X-Telegram-User-Id", required = false) Long userId) {
        log.info("Deleting game with id: {} by user: {}", id, userId);

        if (!isAdmin(userId)) {
            log.warn("Unauthorized attempt to delete game by user: {}", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            gameService.deleteGame(id);
            log.info("Successfully deleted game with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Game not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isAdmin(Long userId) {
        return userId != null && ADMIN_IDS.contains(userId);
    }
}