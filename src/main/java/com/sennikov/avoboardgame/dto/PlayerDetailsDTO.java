package com.sennikov.avoboardgame.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlayerDetailsDTO {
    private String username;
    private String fullName;
    private List<GameStatsDTO> topGames;
    private List<GameStatsDTO> topWinningGames;
} 