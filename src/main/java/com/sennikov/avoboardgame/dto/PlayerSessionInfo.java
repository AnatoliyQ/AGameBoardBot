package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSessionInfo {
    private String username;
    private Boolean isWinner;
}
