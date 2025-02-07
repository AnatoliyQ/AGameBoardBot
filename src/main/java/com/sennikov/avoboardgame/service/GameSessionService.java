package com.sennikov.avoboardgame.service;

import com.sennikov.avoboardgame.dto.*;
import com.sennikov.avoboardgame.exception.ResourceNotFoundException;
import com.sennikov.avoboardgame.mapper.GameSessionMapper;
import com.sennikov.avoboardgame.mapper.StatisticsMapper;
import com.sennikov.avoboardgame.model.GameSession;
import com.sennikov.avoboardgame.model.GameSessionPlayer;
import com.sennikov.avoboardgame.repository.BoardGameRepository;
import com.sennikov.avoboardgame.repository.GameSessionRepository;
import com.sennikov.avoboardgame.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class GameSessionService {
    private final GameSessionRepository sessionRepository;
    private final GameSessionMapper mapper;
    private final StatisticsMapper statisticsMapper;
    private final BoardGameRepository gameRepository;
    private final UserRepository userRepository;

    @Transactional
    public GameSessionResponse saveSession(GameSessionRequest request) {
        if (!gameRepository.existsById(request.getGameId())) {
            throw new ResourceNotFoundException("Game not found with id: " + request.getGameId());
        }

        GameSession session = new GameSession();
        session.setGameId(request.getGameId());
        session.setPlayDate(LocalDate.parse(request.getPlayDate()));

        final GameSession savedSession = sessionRepository.save(session);

        if (request.getPlayers() != null) {
            List<GameSessionPlayer> players = request.getPlayers().stream()
                    .map(info -> {
                        GameSessionPlayer player = new GameSessionPlayer();
                        player.setPlayerUsername(info.getUsername());
                        player.setIsWinner(info.getIsWinner());
                        player.setSession(savedSession);
                        player.setSessionId(savedSession.getId());
                        return player;
                    })
                    .collect(Collectors.toList());
            savedSession.setPlayers(players);
        }

        return mapper.toResponse(sessionRepository.save(savedSession));
    }

    public List<GameSessionResponse> getAllSessions() {
        return mapper.toResponseList(sessionRepository.findAllByOrderByPlayDateDesc());
    }

    @Transactional(readOnly = true)
    public List<PlayerWinsDTO> getTopWinners() {
        return sessionRepository.findPlayerWinsStatistics().stream()
                .map(statisticsMapper::toPlayerWinsDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerStatsDTO> getTopPlayers() {
        return sessionRepository.findPlayerGameStatistics().stream()
                .map(statisticsMapper::toPlayerStatsDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GameStatsDTO> getTopGames() {
        return sessionRepository.findGameStatistics().stream()
                .map(statisticsMapper::toGameStatsDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlayerDetailsDTO getPlayerDetails(String username) {
        PlayerDetailsDTO details = new PlayerDetailsDTO();
        
        // Получаем информацию о пользователе
        var user = userRepository.findUserByTelegramUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        details.setUsername(user.getTelegramUsername());
        details.setFullName(user.getFirstName() + " " + user.getLastName());
        
        // Получаем топ игр
        details.setTopGames(
            sessionRepository.findTopGamesByPlayer(username).stream()
                .map(statisticsMapper::toGameStatsDTO)
                .collect(Collectors.toList())
        );
        
        // Получаем топ игр по победам
        details.setTopWinningGames(
            sessionRepository.findTopWinningGamesByPlayer(username).stream()
                .map(statisticsMapper::toGameStatsDTO)
                .collect(Collectors.toList())
        );
        
        return details;
    }
}