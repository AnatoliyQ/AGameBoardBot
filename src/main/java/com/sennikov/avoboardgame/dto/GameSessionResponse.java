package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionResponse {
    private Long id;
    private Long gameId;
    private String gameName;
    private LocalDate playDate;
    private List<PlayerSessionInfo> players;
}
