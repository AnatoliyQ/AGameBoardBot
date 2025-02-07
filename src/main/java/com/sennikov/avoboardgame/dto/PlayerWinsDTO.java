package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerWinsDTO {
    private String username;
    private String fullName;
    private Long pvpWins;
    private Long pveWins;
    private Long teamWins;
} 