package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionRequest {
    private Long gameId;
    private String playDate;
    private Boolean isVictory;
    private String winningTeam;
    private String winnerUsername;
    private String teamsInfo;
    private List<PlayerSessionInfo> players;
}
