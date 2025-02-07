package com.sennikov.avoboardgame.mapper;

import com.sennikov.avoboardgame.dto.GameSessionRequest;
import com.sennikov.avoboardgame.dto.GameSessionResponse;
import com.sennikov.avoboardgame.dto.PlayerSessionInfo;
import com.sennikov.avoboardgame.model.GameSession;
import com.sennikov.avoboardgame.model.GameSessionPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GameSessionMapper {

    public GameSession toEntity(GameSessionRequest request) {
        if (request == null) {
            return null;
        }

        GameSession session = new GameSession();
        session.setGameId(request.getGameId());
        session.setPlayDate(LocalDate.parse(request.getPlayDate()));

        if (request.getPlayers() != null) {
            List<GameSessionPlayer> players = request.getPlayers().stream()
                    .map(info -> createPlayerEntity(info, session))
                    .collect(Collectors.toList());
            session.setPlayers(players);
        }

        return session;
    }

    public GameSessionResponse toResponse(GameSession session) {
        if (session == null) {
            return null;
        }

        return new GameSessionResponse(
                session.getId(),
                session.getGameId(),
                null, // gameName будет заполняться в сервисе
                session.getPlayDate(),
                session.getPlayers().stream()
                        .map(this::toPlayerInfo)
                        .collect(Collectors.toList())
        );
    }

    public List<GameSessionResponse> toResponseList(List<GameSession> sessions) {
        if (sessions == null) {
            return Collections.emptyList();
        }
        return sessions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private GameSessionPlayer createPlayerEntity(PlayerSessionInfo info, GameSession session) {
        if (info == null) {
            return null;
        }

        GameSessionPlayer player = new GameSessionPlayer();
        player.setPlayerUsername(info.getUsername());
        player.setIsWinner(info.getIsWinner());
        player.setSession(session);
        if (session.getId() != null) {
            player.setSessionId(session.getId());
        }
        return player;
    }

    private PlayerSessionInfo toPlayerInfo(GameSessionPlayer player) {
        if (player == null) {
            return null;
        }

        return new PlayerSessionInfo(
                player.getPlayerUsername(),
                player.getIsWinner()
        );
    }
}
