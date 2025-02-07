package com.sennikov.avoboardgame.mapper;

import com.sennikov.avoboardgame.dto.GameStatsDTO;
import com.sennikov.avoboardgame.dto.PlayerStatsDTO;
import com.sennikov.avoboardgame.dto.PlayerWinsDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StatisticsMapper {

    public PlayerWinsDTO toPlayerWinsDTO(Map<String, Object> stats) {
        PlayerWinsDTO dto = new PlayerWinsDTO();
        dto.setUsername((String) stats.get("username"));
        dto.setFullName((String) stats.get("fullName"));
        dto.setPvpWins(convertToLong(stats.get("pvpWins")));
        dto.setPveWins(convertToLong(stats.get("pveWins")));
        return dto;
    }

    public PlayerStatsDTO toPlayerStatsDTO(Map<String, Object> stats) {
        PlayerStatsDTO dto = new PlayerStatsDTO();
        dto.setUsername((String) stats.get("username"));
        dto.setFullName((String) stats.get("fullName"));
        dto.setDaysPlayed(convertToLong(stats.get("daysPlayed")));
        dto.setGamesPlayed(convertToLong(stats.get("gamesPlayed")));
        return dto;
    }

    public GameStatsDTO toGameStatsDTO(Map<String, Object> stats) {
        GameStatsDTO dto = new GameStatsDTO();
        dto.setId(convertToLong(stats.get("id")));
        dto.setName((String) stats.get("name"));
        
        // Маппим все возможные счетчики
        Long gamesCount = convertToLong(stats.get("gamesCount"));
        Long winsCount = convertToLong(stats.get("winsCount"));
        Long sessionsCount = convertToLong(stats.get("sessionsCount"));
        
        dto.setCount(gamesCount);        // для топ игр игрока
        dto.setWinsCount(winsCount);     // для топ выигрышей
        dto.setSessionsCount(sessionsCount); // для общей статистики
        
        return dto;
    }

    private Long convertToLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
} 