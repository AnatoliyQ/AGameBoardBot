package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStatsDTO {
    private Long id;
    private String name;
    private Long count;      // для общего количества игр
    private Long winsCount;  // для количества побед
    private Long sessionsCount; // для статистики по играм
} 