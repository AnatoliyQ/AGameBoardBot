package com.sennikov.avoboardgame.controller;

import com.sennikov.avoboardgame.dto.GameSessionRequest;
import com.sennikov.avoboardgame.dto.GameSessionResponse;
import com.sennikov.avoboardgame.dto.PlayerWinsDTO;
import com.sennikov.avoboardgame.dto.PlayerStatsDTO;
import com.sennikov.avoboardgame.dto.GameStatsDTO;
import com.sennikov.avoboardgame.dto.PlayerDetailsDTO;
import com.sennikov.avoboardgame.service.GameSessionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Slf4j
@AllArgsConstructor
public class GameStatisticsController {
    private final GameSessionService sessionService;
    private static final List<Long> ADMIN_IDS = Arrays.asList(332120503L, 1204992915L);

    @PostMapping("/sessions")
    public ResponseEntity<GameSessionResponse> addSession(
            @RequestBody GameSessionRequest request,
            @RequestHeader("X-Telegram-User-Id") Long userId) {
        if (!ADMIN_IDS.contains(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("Adding new game session: {}", request);
        return ResponseEntity.ok(sessionService.saveSession(request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<GameSessionResponse>> getAllSessions(
            @RequestHeader("X-Telegram-User-Id") Long userId) {

        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/winners")
    public ResponseEntity<List<PlayerWinsDTO>> getTopWinners(
            @RequestHeader("X-Telegram-User-Id") Long userId) {

        return ResponseEntity.ok(sessionService.getTopWinners());
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerStatsDTO>> getTopPlayers(
            @RequestHeader("X-Telegram-User-Id") Long userId) {

        return ResponseEntity.ok(sessionService.getTopPlayers());
    }

    @GetMapping("/games")
    public ResponseEntity<List<GameStatsDTO>> getTopGames(
            @RequestHeader("X-Telegram-User-Id") Long userId) {

        return ResponseEntity.ok(sessionService.getTopGames());
    }

    @GetMapping("/players/{username}")
    public ResponseEntity<PlayerDetailsDTO> getPlayerDetails(
            @PathVariable String username,
            @RequestHeader("X-Telegram-User-Id") Long userId) {
        return ResponseEntity.ok(sessionService.getPlayerDetails(username));
    }
}
